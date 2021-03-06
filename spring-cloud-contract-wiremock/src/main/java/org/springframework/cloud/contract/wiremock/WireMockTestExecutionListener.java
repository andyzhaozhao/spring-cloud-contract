/*
 * Copyright 2013-2018 the original author or authors.
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
 *
 */

package org.springframework.cloud.contract.wiremock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Dirties the test context if WireMock was running on a fixed port
 *
 * @author Marcin Grzejszczak
 * @since 1.2.6
 */
public final class WireMockTestExecutionListener extends AbstractTestExecutionListener {

	private static final Log log = LogFactory.getLog(WireMockTestExecutionListener.class);

	@Override
	public void afterTestClass(TestContext testContext) {
		if (wireMockConfigurationMissing(testContext) || annotationMissing(testContext)) {
			return;
		}
		if (portIsFixed(testContext)) {
			if (log.isWarnEnabled()) {
				log.warn("You've used fixed ports for WireMock setup - "
						+ "will mark context as dirty. Please use random ports, as much "
						+ "as possible. Your tests will be faster and more reliable and this"
						+ "warning will go away");
			}
			testContext.markApplicationContextDirty(DirtiesContext.HierarchyMode.EXHAUSTIVE);
		}
	}

	private boolean annotationMissing(TestContext testContext) {
		if (testContext.getTestClass()
				.getAnnotationsByType(AutoConfigureWireMock.class).length == 0) {
			if (log.isDebugEnabled()) {
				log.debug("No @AutoConfigureWireMock annotation found on [" + testContext
						.getTestClass() + "]. Skipping");
			}
			return true;
		}
		return false;
	}

	private boolean wireMockConfigurationMissing(TestContext testContext) {
		boolean missing = !testContext.getApplicationContext()
				.containsBean(WireMockConfiguration.class.getName());
		if (log.isDebugEnabled()) {
			log.debug("WireMockConfiguration is missing [" + missing + "]");
		}
		return missing;
	}

	private WireMockConfiguration wireMockConfig(TestContext testContext) {
		return testContext.getApplicationContext().getBean(WireMockConfiguration.class);
	}

	private boolean portIsFixed(TestContext testContext) {
		WireMockConfiguration wireMockProperties = wireMockConfig(testContext);
		int httpPort = wireMockProperties.wireMock.getServer().getPort();
		int httpsPort = wireMockProperties.wireMock.getServer().getHttpsPort();
		return (httpPort != 0 || httpsPort != -1) && httpsPort != 0;
	}
}