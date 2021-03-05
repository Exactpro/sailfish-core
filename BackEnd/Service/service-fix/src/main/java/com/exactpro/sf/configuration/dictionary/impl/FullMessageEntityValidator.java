/******************************************************************************
 * Copyright 2009-2020 Exactpro (Exactpro Systems Limited)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.exactpro.sf.configuration.dictionary.impl;

import com.exactpro.sf.common.messages.structures.IDictionaryStructure;
import com.exactpro.sf.common.messages.structures.IFieldStructure;
import com.exactpro.sf.common.messages.structures.IMessageStructure;
import com.exactpro.sf.configuration.dictionary.DictionaryValidationError;
import com.exactpro.sf.configuration.dictionary.DictionaryValidationErrorLevel;
import com.exactpro.sf.configuration.dictionary.DictionaryValidationErrorType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import java.util.Iterator;
import java.util.List;

public class FullMessageEntityValidator extends BaseMessageEntityValidator {

    @Override
    public void validateEntity(List<DictionaryValidationError> errors, IDictionaryStructure dictionary,
                               IMessageStructure message) {
        super.validateEntity(errors, dictionary, message);
        checkUniqueness(errors, message);
    }

    private void checkUniqueness(List<DictionaryValidationError> errors, IMessageStructure message) {

        SetMultimap<String, String> duplicates = HashMultimap.create();

        checkFields(message, duplicates);

        if (!duplicates.isEmpty()) {
            for (String fieldName : duplicates.keySet()) {
                if (duplicates.get(fieldName).size() > 1) {
                    StringBuilder error = new StringBuilder(
                            String.format("Field <strong>[%s]</strong> in <strong>[%s]</strong> message duplicated in ",
                                    fieldName, message.getName()));
                    for (Iterator<String> i = duplicates.get(fieldName).iterator(); i.hasNext();) {
                        error.append(String.format("<strong>[%s]</strong>", i.next()));
                        if (i.hasNext()) {
                            error.append(", ");
                        }
                    }
                    errors.add(new DictionaryValidationError(message.getName(), fieldName, error.toString(),
                            DictionaryValidationErrorLevel.MESSAGE, DictionaryValidationErrorType.ERR_DUPLICATE_NAME));
                }
            }
        }
    }

    private void checkFields(IFieldStructure message, SetMultimap<String, String> duplicates) {
        for(IFieldStructure messageField : message.getFields().values()) {
            if (messageField.isComplex()) {
                checkFields(messageField, duplicates);
            } else {
                checkFieldRepeating(message, messageField, duplicates);
            }
        }
    }

    private void checkFieldRepeating(IFieldStructure message, IFieldStructure field,
                                     SetMultimap<String, String> duplicates) {
        for(IFieldStructure messageField : message.getFields().values()) {
            if (messageField.isComplex()) {
                checkFieldRepeating(messageField, field, duplicates);
            } else {
                if (field.getName().equals(messageField.getName())
                        && field.getReferenceName() != null
                        && field.getReferenceName().equals(messageField.getReferenceName())) {
                    duplicates.put(field.getName(), message.getName());
                }
            }
        }
    }
}
