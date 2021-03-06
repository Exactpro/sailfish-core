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
package com.exactpro.sf.configuration.dictionary;

import com.exactpro.sf.common.messages.structures.IDictionaryStructure;
import com.exactpro.sf.configuration.dictionary.interfaces.IDictionaryValidator;
import com.exactpro.sf.services.ntg.TestNTGHelper;
import com.exactpro.sf.util.AbstractTest;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestNTGDictionaryValidator extends AbstractTest {

    private final IDictionaryValidator validator = new NTGDictionaryValidatorFactory()
            .createDictionaryValidator();

    @Test
    public void testNTGDictionaryValidatorPositive() throws IOException {
        IDictionaryStructure dictionary = TestNTGHelper.getTestValidDictionary();

        List<DictionaryValidationError> errors = validator.validate(dictionary, true, null);

        assertEquals(0, errors.size());
    }

    @Test
    public void testNTGDictionaryValidatorErrors() throws IOException {
        IDictionaryStructure dictionary = TestNTGHelper.getTestInvalidDictionary();

        List<DictionaryValidationError> errors = validator.validate(dictionary, true, null);

        String[] expectedErrors = {
                "DictionaryValidationError[message=Logon,field=MessageVersion,error=Field's type [JAVA_LANG_BYTE] cannot be matched to any unsigned type. Use [JAVA_LANG_SHORT] for field's type with current Type attribute,level=FIELD,type=ERR_ATTRIBUTES]",
                "DictionaryValidationError[message=Reject,field=RejectCode,error=Use [UInt8, UInt16] for Type attribute with current field's type or change field's type to [JAVA_LANG_LONG] to use with current Type attribute value,level=FIELD,type=ERR_ATTRIBUTES]",
                "DictionaryValidationError[message=Reject,field=<null>,error=Message  <strong>\"Reject\"</strong> doesn't contain IsAdmin attribute,level=MESSAGE,type=ERR_ATTRIBUTES]",
                "DictionaryValidationError[message=AnotherTestMessage,field=OrderType,error=Attribute <strong>\"Length\"</strong> has incorrect value = [8]. Must be one of [1, 2, 4],level=FIELD,type=ERR_ATTRIBUTES]",
                "DictionaryValidationError[message=AnotherTestMessage,field=OrderType,error=Use [UInt8, UInt16] for Type attribute with current field's type or change field's type to [JAVA_MATH_BIG_DECIMAL] to use with current Type attribute value,level=FIELD,type=ERR_ATTRIBUTES]",
                "DictionaryValidationError[message=AnotherTestMessage,field=AnotherOrderType,error=Attribute <strong>\"Length\"</strong> has incorrect value = [1]. Must be one of [4, 8],level=FIELD,type=ERR_ATTRIBUTES]",
                "DictionaryValidationError[message=AnotherTestMessage,field=AnotherOrderType,error=Use [UInt32] for Type attribute with current field's type or change field's type to [JAVA_MATH_BIG_DECIMAL] to use with current Type attribute value,level=FIELD,type=ERR_ATTRIBUTES]",
                "DictionaryValidationError[message=AnotherTestMessage,field=AnotherOrderType,error=Attribute <strong>\"Type\"</strong> value [Uint64] has length different from the attribute <strong>\"Length\"</strong> value = [1],level=FIELD,type=ERR_ATTRIBUTES]",
                "DictionaryValidationError[message=TestMessage,field=ClOrdID,error=Attribute <strong>Format</strong> must have one of the next values [A,D] for field with type [java.lang.String] but has [S],level=FIELD,type=ERR_ATTRIBUTES]",
                "DictionaryValidationError[message=TestMessage,field=OrderType,error=Offset attribute is incorrect. actual - 50; expected - 54,level=FIELD,type=ERR_ATTRIBUTES]",
                "DictionaryValidationError[message=<null>,field=<null>,error=Message  <strong>\"Heartbeat\"</strong> is missing in dictionary,level=DICTIONARY,type=ERR_REQUIRED_FIELD]",
                "DictionaryValidationError[message=Reject,field=<null>,error=MessageType attribute value is not exist in enum. Value [52],level=MESSAGE,type=ERR_ATTRIBUTES]"

        };

        assertEquals("Wrong errors count", expectedErrors.length, errors.size());
        for (DictionaryValidationError error : errors) {
            assertTrue("Error '" + error + "' wasn't found", ArrayUtils.contains(expectedErrors, error.toString()));
        }
    }
}
