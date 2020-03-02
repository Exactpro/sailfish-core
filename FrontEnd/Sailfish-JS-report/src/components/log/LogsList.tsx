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

import * as React from 'react';
import { connect } from 'react-redux';
import AppState from '../../state/models/AppState';
import { getLogsCount } from '../../selectors/logs';
import { Virtuoso } from 'react-virtuoso';
import { loadLogs } from '../../thunks/loadTestCase';
import { ThunkDispatch } from 'redux-thunk';
import StateAction from '../../actions/stateActions';
import SkeletonedLogsListItem from './SkeletonedLogsListItem';
import '../../styles/log.scss'

interface StateProps {
    logsCount: number;
    scrolledIndex: Number | null;
}

interface DispatchProps {
    loadLogs: () => void;
}

interface Props extends StateProps, DispatchProps {
    isActive: boolean;
}

function LogsListBase({
    logsCount,
    isActive,
    loadLogs,
    scrolledIndex
}: Props) {
    const [loadingLogs, setLoadingLogs] = React.useState(false);
    const virtuoso = React.useRef<Virtuoso>();

    React.useEffect(() => {
        if (isActive && !loadingLogs) {
            loadLogs();
            setLoadingLogs(true);
        }
    }, [isActive]);

    React.useEffect(() => {
        if (scrolledIndex != null) {
            virtuoso.current?.scrollToIndex(scrolledIndex.valueOf());
        }
    }, [scrolledIndex]);

    const renderLog = (idx: number): React.ReactElement => {
        return <SkeletonedLogsListItem index={idx} />
    };

    return (
        <div className="logs">
            <div className="logs__list">
                <Virtuoso
                    ref={virtuoso}
                    totalCount={logsCount}
                    item={renderLog}
                    overscan={3}
                    style={{height: '100%'}}
                    className="logs__scroll-container"
                />
            </div>
        </div>
    )
}

const LogsList = connect(
    (state: AppState): StateProps => ({
        logsCount: getLogsCount(state),
        scrolledIndex: state.selected.scrolledLogIndex
    }),
    (dispatch: ThunkDispatch<AppState, {}, StateAction>): DispatchProps => ({
        loadLogs: () => dispatch(loadLogs()),
    }),
    (stateProps, dispatchProps, ownProps) => ({ ...stateProps, ...dispatchProps, ...ownProps}),
)(LogsListBase);

export default LogsList;
