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
package com.exactpro.sf.scriptrunner;

import com.exactpro.sf.configuration.suri.SailfishURI;

public class TestScriptProgressListener implements IProgressListener {

    private final TestScriptDescription description;

	public TestScriptProgressListener(TestScriptDescription description) {
		this.description = description;
	}

	@Override
	public void onProgressChanged(int progress) {
        description.setProgress(progress);
	}

    @Override
    public void onDetermineLanguage(SailfishURI languageURI) {
        description.setLanguageURI(languageURI);
    }
}
