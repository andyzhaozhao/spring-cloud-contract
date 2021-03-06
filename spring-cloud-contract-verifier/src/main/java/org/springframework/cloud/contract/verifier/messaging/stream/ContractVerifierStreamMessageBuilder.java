/*
 *  Copyright 2013-2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.springframework.cloud.contract.verifier.messaging.stream;

import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @author Marcin Grzejszczak
 */
class ContractVerifierStreamMessageBuilder {

	public <T> Message<?> create(T payload, Map<String, Object> headers) {
		Object payloadToSend = payload;
		if (payload instanceof String) {
			payloadToSend = ((String) payload).getBytes();
		}
		return MessageBuilder.createMessage(payloadToSend, new MessageHeaders(headers));
	}

}
