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

package com.ritense.valtimoplugins.pivadapter.plugin

import com.ritense.plugin.annotation.Plugin
import com.ritense.plugin.annotation.PluginAction
import com.ritense.plugin.annotation.PluginActionProperty
import com.ritense.plugin.annotation.PluginProperty
import com.ritense.processlink.domain.ActivityTypeWithEventName.SERVICE_TASK_START
import com.ritense.valtimoplugins.pivadapter.client.PivAdapterService
import com.ritense.valtimoplugins.pivadapter.client.models.Adresgegevens
import com.ritense.valtimoplugins.pivadapter.client.models.BinnenVerhuisaanvraagGegevens
import com.ritense.valtimoplugins.pivadapter.client.models.BinnengemeentelijkeVerhuisAanvraag
import com.ritense.valtimoplugins.pivadapter.client.models.BuitenVerhuisaanvraagGegevens
import com.ritense.valtimoplugins.pivadapter.client.models.BuitengemeentelijkeMeeverhuizer
import com.ritense.valtimoplugins.pivadapter.client.models.BuitengemeentelijkeVerhuisAanvraag
import com.ritense.valtimoplugins.pivadapter.client.models.Contactgegevens
import com.ritense.valtimoplugins.pivadapter.client.models.FunctieAdres
import com.ritense.valtimoplugins.pivadapter.client.models.HuidigeAdresgegevens
import com.ritense.valtimoplugins.pivadapter.client.models.Huisaanduiding
import com.ritense.valtimoplugins.pivadapter.client.models.Uittrekselaanvraag
import com.ritense.valtimoplugins.pivadapter.client.models.UittrekselaanvraagGegeven
import com.ritense.valtimoplugins.pivadapter.client.models.ZaakGegevens
import io.github.oshai.kotlinlogging.KotlinLogging
import org.operaton.bpm.engine.delegate.DelegateExecution
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

private const val DEFAULT_STATUS_PROCESS_VARIABLE = "pivAdapterStatus"

/**
 * Plugin exposing the PIV Adapter API, which is used to send requests for uittreksels,
 * a binnengemeentelijke verhuizing or a buitengemeentelijke verhuizing to PIV.
 * Note that the key in the @Plugin annotation must be unique, and
 * should be equal to the pluginId in the plugin's frontend configuration.
 */
@Plugin(
    key = "piv-adapter",
    title = "PIV Adapter Plugin",
    description = "Stuurt verzoeken om uittreksels of een verhuizing naar PIV.",
)
open class PivAdapterPlugin(
    private val pivAdapterService: PivAdapterService,
) {
    /**
     * Base URL of the PIV Adapter API, including the servlet path of the specification,
     * for example https://piv.example.nl/esuite/pivadapter
     */
    @PluginProperty(key = "pivAdapterBaseUrl", secret = false)
    lateinit var pivAdapterBaseUrl: String

    /**
     * Requests one or more uittreksels at PIV. Maps to POST /v1/uittrekselAanvraag.
     */
    @PluginAction(
        key = "piv-uittreksel-aanvraag",
        title = "Uittreksel aanvragen",
        description = "Stuurt een verzoek om 1 of meer uittreksels aan te vragen naar PIV.",
        activityTypes = [SERVICE_TASK_START],
    )
    open fun uittrekselAanvraag(
        @PluginActionProperty zaakId: String,
        @PluginActionProperty datumAanvraag: LocalDate,
        @PluginActionProperty aanvraaggegevens: List<UittrekselaanvraagGegeven>,
        @PluginActionProperty emailadres: String,
        @PluginActionProperty telefoonnummerPrive: String,
        @PluginActionProperty telefoonnummerWerk: String?,
        @PluginActionProperty telefoonnummerMobiel: String?,
    ) {
        logger.debug { "Sending uittrekselAanvraag for zaak $zaakId with ${aanvraaggegevens.size} uittreksel(s)" }

        pivAdapterService.uittrekselAanvraag(
            baseUrl = pivAdapterBaseUrl,
            aanvraag =
                Uittrekselaanvraag(
                    zaakgegevens = zaakGegevens(zaakId, datumAanvraag),
                    aanvraaggegevens = aanvraaggegevens,
                    contactgegevens =
                        contactgegevens(
                            emailadres,
                            telefoonnummerPrive,
                            telefoonnummerWerk,
                            telefoonnummerMobiel,
                        ),
                ),
        )
    }

    /**
     * Reports a move within the same municipality. Maps to POST /v1/binnenVerhuisAanvraag.
     */
    @PluginAction(
        key = "piv-binnen-verhuis-aanvraag",
        title = "Binnengemeentelijke verhuizing doorgeven",
        description = "Stuurt een verzoek om een binnengemeentelijke verhuizing naar PIV.",
        activityTypes = [SERVICE_TASK_START],
    )
    open fun binnenVerhuisAanvraag(
        @PluginActionProperty zaakId: String,
        @PluginActionProperty datumAanvraag: LocalDate,
        @PluginActionProperty burgerservicenummerAanvrager: String,
        @PluginActionProperty verhuisdatum: LocalDate,
        @PluginActionProperty indicatieInwonend: Boolean,
        @PluginActionProperty postcode: String,
        @PluginActionProperty huisnummer: Int,
        @PluginActionProperty huisletter: String?,
        @PluginActionProperty huisaanduiding: Huisaanduiding?,
        @PluginActionProperty huistoevoeging: String?,
        @PluginActionProperty functieAdres: FunctieAdres?,
        @PluginActionProperty meeverhuizerBsns: List<String>?,
        @PluginActionProperty emailadres: String,
        @PluginActionProperty telefoonnummerPrive: String,
        @PluginActionProperty telefoonnummerWerk: String?,
        @PluginActionProperty telefoonnummerMobiel: String?,
    ) {
        logger.debug { "Sending binnenVerhuisAanvraag for zaak $zaakId" }

        pivAdapterService.binnenVerhuisAanvraag(
            baseUrl = pivAdapterBaseUrl,
            aanvraag =
                BinnengemeentelijkeVerhuisAanvraag(
                    zaakgegevens = zaakGegevens(zaakId, datumAanvraag),
                    aanvraaggegevens =
                        BinnenVerhuisaanvraagGegevens(
                            burgerservicenummerAanvrager = burgerservicenummerAanvrager,
                            verhuisdatum = verhuisdatum,
                            indicatieInwonend = indicatieInwonend,
                            adresgegevens =
                                adresgegevens(
                                    postcode,
                                    huisnummer,
                                    huisletter,
                                    huisaanduiding,
                                    huistoevoeging,
                                    functieAdres,
                                ),
                            meeverhuizerBsns = meeverhuizerBsns,
                        ),
                    contactgegevens =
                        contactgegevens(
                            emailadres,
                            telefoonnummerPrive,
                            telefoonnummerWerk,
                            telefoonnummerMobiel,
                        ),
                ),
        )
    }

    /**
     * Reports a move to another municipality. Maps to POST /v1/buitenVerhuisAanvraag.
     */
    @PluginAction(
        key = "piv-buiten-verhuis-aanvraag",
        title = "Buitengemeentelijke verhuizing doorgeven",
        description = "Stuurt een verzoek om een buitengemeentelijke verhuizing naar PIV.",
        activityTypes = [SERVICE_TASK_START],
    )
    open fun buitenVerhuisAanvraag(
        @PluginActionProperty zaakId: String,
        @PluginActionProperty datumAanvraag: LocalDate,
        @PluginActionProperty burgerservicenummerAanvrager: String,
        @PluginActionProperty gemeentecodeAanvrager: String,
        @PluginActionProperty verhuisdatum: LocalDate,
        @PluginActionProperty indicatieInwonend: Boolean,
        @PluginActionProperty huidigeStraatnaam: String,
        @PluginActionProperty huidigePostcode: String,
        @PluginActionProperty huidigeHuisnummer: Int,
        @PluginActionProperty huidigeHuisletter: String?,
        @PluginActionProperty huidigeHuisaanduiding: Huisaanduiding?,
        @PluginActionProperty huidigeHuistoevoeging: String?,
        @PluginActionProperty postcode: String,
        @PluginActionProperty huisnummer: Int,
        @PluginActionProperty huisletter: String?,
        @PluginActionProperty huisaanduiding: Huisaanduiding?,
        @PluginActionProperty huistoevoeging: String?,
        @PluginActionProperty functieAdres: FunctieAdres?,
        @PluginActionProperty buitengemeentelijkeMeeverhuizers: List<BuitengemeentelijkeMeeverhuizer>?,
        @PluginActionProperty emailadres: String,
        @PluginActionProperty telefoonnummerPrive: String,
        @PluginActionProperty telefoonnummerWerk: String?,
        @PluginActionProperty telefoonnummerMobiel: String?,
    ) {
        logger.debug { "Sending buitenVerhuisAanvraag for zaak $zaakId" }

        pivAdapterService.buitenVerhuisAanvraag(
            baseUrl = pivAdapterBaseUrl,
            aanvraag =
                BuitengemeentelijkeVerhuisAanvraag(
                    zaakgegevens = zaakGegevens(zaakId, datumAanvraag),
                    aanvraaggegevens =
                        BuitenVerhuisaanvraagGegevens(
                            burgerservicenummerAanvrager = burgerservicenummerAanvrager,
                            gemeentecodeAanvrager = gemeentecodeAanvrager,
                            verhuisdatum = verhuisdatum,
                            indicatieInwonend = indicatieInwonend,
                            huidigeAdresgegevens =
                                HuidigeAdresgegevens(
                                    straatnaam = huidigeStraatnaam,
                                    postcode = huidigePostcode,
                                    huisnummer = huidigeHuisnummer,
                                    huisletter = huidigeHuisletter,
                                    huisaanduiding = huidigeHuisaanduiding,
                                    huistoevoeging = huidigeHuistoevoeging,
                                ),
                            adresgegevens =
                                adresgegevens(
                                    postcode,
                                    huisnummer,
                                    huisletter,
                                    huisaanduiding,
                                    huistoevoeging,
                                    functieAdres,
                                ),
                            buitengemeentelijkeMeeverhuizers = buitengemeentelijkeMeeverhuizers,
                        ),
                    contactgegevens =
                        contactgegevens(
                            emailadres,
                            telefoonnummerPrive,
                            telefoonnummerWerk,
                            telefoonnummerMobiel,
                        ),
                ),
        )
    }

    /**
     * Checks whether the PIV Adapter endpoint is reachable and stores the reported application and
     * version as a process variable. Maps to GET /v1/status.
     */
    @PluginAction(
        key = "piv-status",
        title = "PIV Adapter status opvragen",
        description = "Test of de verbinding met het PIV Adapter endpoint in orde is.",
        activityTypes = [SERVICE_TASK_START],
    )
    open fun status(
        execution: DelegateExecution,
        @PluginActionProperty resultProcessVariable: String?,
    ) {
        val status = pivAdapterService.status(baseUrl = pivAdapterBaseUrl)
        logger.debug { "PIV Adapter status: application ${status.application}, version ${status.version}" }

        execution.setVariable(
            resultProcessVariable ?: DEFAULT_STATUS_PROCESS_VARIABLE,
            mapOf(
                "application" to status.application,
                "version" to status.version,
            ),
        )
    }

    private fun zaakGegevens(
        zaakId: String,
        datumAanvraag: LocalDate,
    ) = ZaakGegevens(
        zaakID = zaakId,
        datumAanvraag = datumAanvraag,
    )

    private fun contactgegevens(
        emailadres: String,
        telefoonnummerPrive: String,
        telefoonnummerWerk: String?,
        telefoonnummerMobiel: String?,
    ) = Contactgegevens(
        emailadres = emailadres,
        telefoonnummerPrive = telefoonnummerPrive,
        telefoonnummerWerk = telefoonnummerWerk,
        telefoonnummerMobiel = telefoonnummerMobiel,
    )

    private fun adresgegevens(
        postcode: String,
        huisnummer: Int,
        huisletter: String?,
        huisaanduiding: Huisaanduiding?,
        huistoevoeging: String?,
        functieAdres: FunctieAdres?,
    ) = Adresgegevens(
        postcode = postcode,
        huisnummer = huisnummer,
        huisletter = huisletter,
        huisaanduiding = huisaanduiding,
        huistoevoeging = huistoevoeging,
        functieAdres = functieAdres,
    )
}
