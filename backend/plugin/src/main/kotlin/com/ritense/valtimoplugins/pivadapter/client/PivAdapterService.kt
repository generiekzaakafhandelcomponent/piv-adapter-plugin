/*
 * Copyright 2026 Ritense BV, the Netherlands.
 *
 * Licensed under EUPL, Version 1.2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ritense.valtimoplugins.pivadapter.client

import com.ritense.valtimo.contract.annotation.SkipComponentScan
import com.ritense.valtimoplugins.pivadapter.client.apis.BinnenVerhuisApi
import com.ritense.valtimoplugins.pivadapter.client.apis.BuitenVerhuisApi
import com.ritense.valtimoplugins.pivadapter.client.apis.StatusApi
import com.ritense.valtimoplugins.pivadapter.client.apis.UittrekselApi
import com.ritense.valtimoplugins.pivadapter.client.models.BinnengemeentelijkeVerhuisAanvraag
import com.ritense.valtimoplugins.pivadapter.client.models.BuitengemeentelijkeVerhuisAanvraag
import com.ritense.valtimoplugins.pivadapter.client.models.Status
import com.ritense.valtimoplugins.pivadapter.client.models.Uittrekselaanvraag
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.util.concurrent.ConcurrentHashMap

@SkipComponentScan
@Service
class PivAdapterService {
    private val restClients = ConcurrentHashMap<String, RestClient>()

    /**
     * Requests one or more uittreksels. Corresponds to POST /v1/uittrekselAanvraag.
     */
    fun uittrekselAanvraag(
        baseUrl: String,
        aanvraag: Uittrekselaanvraag,
    ) = UittrekselApi(restClientFor(baseUrl)).uittrekselAanvraag(aanvraag)

    /**
     * Reports a move within the same municipality. Corresponds to POST /v1/binnenVerhuisAanvraag.
     */
    fun binnenVerhuisAanvraag(
        baseUrl: String,
        aanvraag: BinnengemeentelijkeVerhuisAanvraag,
    ) = BinnenVerhuisApi(restClientFor(baseUrl)).binnenVerhuisAanvraag(aanvraag)

    /**
     * Reports a move to another municipality. Corresponds to POST /v1/buitenVerhuisAanvraag.
     */
    fun buitenVerhuisAanvraag(
        baseUrl: String,
        aanvraag: BuitengemeentelijkeVerhuisAanvraag,
    ) = BuitenVerhuisApi(restClientFor(baseUrl)).buitenVerhuisAanvraag(aanvraag)

    /**
     * Checks whether the endpoint is reachable. Corresponds to GET /v1/status.
     */
    fun status(baseUrl: String): Status = StatusApi(restClientFor(baseUrl)).status()

    /**
     * The generated API classes accept a [RestClient]. One client is kept per base URL so that it
     * is not rebuilt on every process step.
     */
    private fun restClientFor(baseUrl: String): RestClient =
        restClients.computeIfAbsent(baseUrl) {
            RestClient
                .builder()
                .baseUrl(it)
                .messageConverters { converters -> converters.add(MappingJackson2HttpMessageConverter()) }
                .build()
        }
}
