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
import createAutorunMiddleware from "./autorun";
import { getFilteredActions } from "../../selectors/actions";
import { getFilteredMessages } from "../../selectors/messages";
import { getSearchTokens } from "../../selectors/search";
import { findAll } from "../../helpers/search/searchEngine";
import { setSearchResults } from "../../actions/actionCreators";

const searchAutorun = createAutorunMiddleware(
    [getFilteredActions, getFilteredMessages, getSearchTokens],
    async (actions, messages, searchTokens) => {
        const results = await findAll(searchTokens, {
            actions,
            messages
        });

        return setSearchResults(results);
    }
);

export default searchAutorun;
