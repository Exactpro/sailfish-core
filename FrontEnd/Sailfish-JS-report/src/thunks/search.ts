/******************************************************************************
 * Copyright 2009-2019 Exactpro (Exactpro Systems Limited)
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

import { ThunkDispatch, ThunkAction } from "redux-thunk";
import StateAction from "../actions/stateActions";
import { setSearchTokens, setSearchResults } from "../actions/actionCreators";
import { findAll } from "../helpers/search/searchEngine";
import AppState from "../state/models/AppState";
import SearchToken from "../models/search/SearchToken";

export function performSearch(searchTokens: SearchToken[]): ThunkAction<void, AppState, unknown, StateAction> {
    return async (dispatch, getState) => {
        const { testCase } = getState().selected;
        
        dispatch(setSearchTokens(searchTokens));
        const results = await findAll(searchTokens, testCase);

        if (searchTokens === getState().selected.search.tokens) {
            dispatch(setSearchResults(results));
        }
    }
}
