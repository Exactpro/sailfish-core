/*******************************************************************************
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
 *  limitations under the License.
 ******************************************************************************/

import AppState from "../state/models/AppState";
import { createSelector } from "reselect";
import { getFilterBlocks, getFilterResults, getIsFilterTransparent, getIsMessageFilterApplied } from "./filter";
import { keyForMessage } from "../helpers/keys";
import { isCheckpointMessage, isRejected } from "../helpers/messageType";
import FilterType from "../models/filter/FilterType";
import Message from "../models/Message";

const EMPTY_MESSAGES_ARRAY = new Array<Message>();

export const getMessages = (state: AppState) => state.selected.testCase?.messages ?? EMPTY_MESSAGES_ARRAY;

export const getMessagesIds = createSelector(
    [getMessages],
    (messages) => messages.map(msg => msg.id)
);

export const getFilteredMessages = createSelector(
    [getMessages, getFilterBlocks, getFilterResults],
    (messages, blocks, results) => {
        if (blocks.some(({ types }) => types.includes(FilterType.MESSAGE))) {
            return messages
                .filter(msg => isCheckpointMessage(msg) || results.includes(keyForMessage(msg.id)))
        }

        return messages;
    }
);

export const getTransparentMessages = createSelector(
    [getMessages, getFilterBlocks, getFilterResults],
    (messages, blocks, results): number[] => {
        if (blocks.some(({ types }) => types.includes(FilterType.MESSAGE))) {
            return messages
                .filter(msg => !isCheckpointMessage(msg) && !results.includes(keyForMessage(msg.id)))
                .map(msg => msg.id);
        }

        return [];
    }
);

export const getRejectedMessages = createSelector(
    [getMessages],
    (messages) => messages.filter(isRejected)
);

export const getMessagesFilesCount = (state: AppState) => state.selected.testCase.files?.message.count || 0;

export const getMessagesCount = createSelector(
    [getIsMessageFilterApplied, getIsFilterTransparent, getFilteredMessages, getMessagesFilesCount],
    (isMessageFilterApplied, isTransparent, getFilteredMessages, messagesFileCount) => 
        isMessageFilterApplied && !isTransparent ? getFilteredMessages.length : messagesFileCount
);
