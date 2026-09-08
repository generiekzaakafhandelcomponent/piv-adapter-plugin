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

import {PluginSpecification} from "@valtimo/plugin";
import {PivAdapterPluginConfigurationComponent} from "./components/piv-adapter-plugin-configuration/piv-adapter-plugin-configuration.component";
import {PivUittrekselAanvraagConfigurationComponent} from "./components/piv-uittreksel-aanvraag-configuration/piv-uittreksel-aanvraag-configuration.component";
import {PIV_ADAPTER_PLUGIN_LOGO_BASE64} from "./assets";

const pivAdapterPluginSpecification: PluginSpecification = {
  pluginId: "piv-adapter",
  pluginConfigurationComponent: PivAdapterPluginConfigurationComponent,
  pluginLogoBase64: PIV_ADAPTER_PLUGIN_LOGO_BASE64,
  functionConfigurationComponents: {
    "piv-uittreksel-aanvraag": PivUittrekselAanvraagConfigurationComponent,
  },
  pluginTranslations: {
    nl: {
      title: "PIV Adapter Plugin",
      description: "Stuurt verzoeken om uittreksels of een verhuizing naar PIV.",
      configurationTitle: "Configuratienaam",
      pivAdapterBaseUrl: "PIV Adapter basis-URL",
      "piv-uittreksel-aanvraag": "Uittreksel aanvragen",
      "piv-binnen-verhuis-aanvraag": "Binnengemeentelijke verhuizing doorgeven",
      "piv-buiten-verhuis-aanvraag": "Buitengemeentelijke verhuizing doorgeven",
      "piv-status": "PIV Adapter status opvragen",
      uittrekselAanvraagDescription:
        "Stuurt een verzoek om 1 of meer uittreksels aan te vragen naar PIV. Tekstvelden mogen ook een waarde-expressie bevatten, bijvoorbeeld doc:/aanvrager/bsn of pv:zaakId.",
      zaakId: "Zaak-ID",
      zaakIdTooltip: "Het zaaknummer waaronder de aanvraag bij PIV bekend is. Maximaal 50 tekens.",
      datumAanvraag: "Datum aanvraag",
      datumAanvraagTooltip: "De datum van de aanvraag in het formaat jjjj-mm-dd.",
      aanvraaggegevens: "Aangevraagde uittreksels",
      aanvraaggegevensTooltip: "Minimaal 1 en maximaal 99 uittreksels per aanvraag.",
      addUittreksel: "Uittreksel toevoegen",
      burgerservicenummer: "Burgerservicenummer",
      uittrekselcode: "Uittrekselcode",
      uittrekselcodeTooltip: "De code van het gevraagde uittreksel, 1 tot 3 cijfers.",
      indicatieGratis: "Gratis",
      indicatieGratisTooltip: "Geeft aan of het uittreksel gratis verstrekt wordt.",
      indicatieGratisJa: "Ja",
      indicatieGratisNee: "Nee",
      emailadres: "E-mailadres",
      telefoonnummerPrive: "Telefoonnummer privé",
      telefoonnummerWerk: "Telefoonnummer werk",
      telefoonnummerMobiel: "Telefoonnummer mobiel",
    },
    en: {
      title: "PIV Adapter Plugin",
      description: "Sends requests for uittreksels or a change of address to PIV.",
      configurationTitle: "Configuration Name",
      pivAdapterBaseUrl: "PIV Adapter base URL",
      "piv-uittreksel-aanvraag": "Request uittreksel",
      "piv-binnen-verhuis-aanvraag": "Report move within municipality",
      "piv-buiten-verhuis-aanvraag": "Report move to another municipality",
      "piv-status": "Request PIV Adapter status",
      uittrekselAanvraagDescription:
        "Sends a request for one or more uittreksels to PIV. Text fields may also contain a value expression, for example doc:/aanvrager/bsn or pv:zaakId.",
      zaakId: "Case ID",
      zaakIdTooltip: "The case number the request is known by at PIV. At most 50 characters.",
      datumAanvraag: "Request date",
      datumAanvraagTooltip: "The date of the request, in yyyy-mm-dd format.",
      aanvraaggegevens: "Requested uittreksels",
      aanvraaggegevensTooltip: "At least 1 and at most 99 uittreksels per request.",
      addUittreksel: "Add uittreksel",
      burgerservicenummer: "Burgerservicenummer",
      uittrekselcode: "Uittreksel code",
      uittrekselcodeTooltip: "The code of the requested uittreksel, 1 to 3 digits.",
      indicatieGratis: "Free of charge",
      indicatieGratisTooltip: "Indicates whether the uittreksel is provided free of charge.",
      indicatieGratisJa: "Yes",
      indicatieGratisNee: "No",
      emailadres: "Email address",
      telefoonnummerPrive: "Phone number (home)",
      telefoonnummerWerk: "Phone number (work)",
      telefoonnummerMobiel: "Phone number (mobile)",
    },
  },
};

export {pivAdapterPluginSpecification};
