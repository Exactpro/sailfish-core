/******************************************************************************
 * Copyright 2009-2018 Exactpro (Exactpro Systems Limited)
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
package com.exactpro.sf.services.fast.filter;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.SequenceValue;
import org.openfast.template.Field;
import org.openfast.template.Group;

import java.util.regex.Pattern;

public class SimpleMessageFilter implements IFastMessageFilter {

    private static final Pattern PATTERN1 = Pattern.compile("^\\[.*\\]");
    private static final Pattern PATTERN2 = Pattern.compile("^!\\[.*\\]");
    //if a field is presents in a message and is a key in requiredFieldValues
    //it must have the value equal to the value in requiredFieldValues be accepted by filter
    private final Multimap<String, String> requiredValues = HashMultimap.create();

    //if a field is presents in a message and is a key in ignoreFieldValues
    //it must have the value not equal to the value in ignoreFieldValues to be accepted by filter
    private final Multimap<String, String> ignoreValues = HashMultimap.create();

	public SimpleMessageFilter() {

    }

    public SimpleMessageFilter(String filterString) {
        if (filterString == null || filterString.isEmpty()) {
            return;
        }
        String[] strvals = filterString.split(";");

        for (String requiredValue : strvals) {
            String[] valuePair = requiredValue.split("=", 2);
            String key = valuePair[0].trim();
            String value = valuePair[1].trim();
            if (PATTERN1.matcher(value).matches()) {
                value = value.substring(1, value.length() - 1);
                requiredValues.put(key, value);
            } else if (PATTERN2.matcher(value).matches()) {
                value = value.substring(2, value.length() - 1);
                ignoreValues.put(key, value);
            } else {
                requiredValues.put(key, value);
            }
        }

    }

    /* (non-Javadoc)
     * @see com.exactpro.sf.services.fast.filter.IFastMessageFilter#isMessageAcceptable(org.openfast.Message)
     */
    @Override
    public boolean isMessageAcceptable(Message fastMsg) {
        if (fastMsg == null) {
            return false;
        }
        if (ignoreValues.isEmpty() && requiredValues.isEmpty()) {
            return true;
        }

        return checkFieldValues(fastMsg);
    }

    private void getAllFields(GroupValue fastMsg, Multimap<String, String> fieldsMultimap) {

        Group group = fastMsg.getGroup();
        for (Field field : group.getFields()) {
            switch (field.getTypeName()) {
                case "scalar":
                case "decimal":
                    if (group.hasField(field.getName())) {
                        if (fastMsg.isDefined(field.getName())) {
                            fieldsMultimap.put(field.getName(), fastMsg.getString(field.getName()));
                        }
                    }
                    break;
                case "group":
                    GroupValue grpValue = fastMsg.getGroup(field.getName());
                    getAllFields(grpValue, fieldsMultimap);
                    break;
                case "sequence":
                    SequenceValue sqsValue = fastMsg.getSequence(field.getName());
                    for (GroupValue groupVal : sqsValue.getValues()) {
                        getAllFields(groupVal, fieldsMultimap);
                    }
                    break;
            }
        }

    }


    private boolean checkFieldValues(GroupValue fastMsg) {
        Multimap<String, String> fieldsMultimap = HashMultimap.create();
        getAllFields(fastMsg, fieldsMultimap);

        if (!checkMapIntersect(fieldsMultimap, requiredValues, true)) return false;

        return checkMapIntersect(fieldsMultimap, ignoreValues, false);
    }


    private boolean checkMapIntersect(Multimap<String, String> fieldsMultimap, Multimap<String, String> filterValues, boolean containsEntries) {
        if (!fieldsMultimap.keySet().containsAll(filterValues.keySet())){
            return true;
        }
        Multimap<String, String> intersectMultimap = Multimaps.filterEntries(fieldsMultimap, e -> filterValues.containsEntry(e.getKey(), e.getValue()));
        if (containsEntries) {
            return intersectMultimap.keySet().size() == filterValues.keySet().size();
        } else {
            return intersectMultimap.isEmpty();
        }

    }

}
