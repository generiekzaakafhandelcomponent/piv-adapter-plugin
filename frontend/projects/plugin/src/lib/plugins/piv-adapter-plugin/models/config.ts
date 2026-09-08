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

import {FunctionConfigurationData, PluginConfigurationData} from "@valtimo/plugin";

interface PivAdapterPluginConfig extends PluginConfigurationData {
  pivAdapterBaseUrl: string;
}

/**
 * A single uittreksel that is requested, as sent to the backend.
 */
interface UittrekselaanvraagGegeven {
  burgerservicenummer: string;
  uittrekselcode: string;
  indicatieGratis?: boolean;
}

/**
 * Action properties of the piv-uittreksel-aanvraag action, as stored on the process link.
 */
interface PivUittrekselAanvraagConfig extends FunctionConfigurationData {
  zaakId: string;
  datumAanvraag: string;
  aanvraaggegevens: Array<UittrekselaanvraagGegeven>;
  emailadres: string;
  telefoonnummerPrive: string;
  telefoonnummerWerk: string | null;
  telefoonnummerMobiel: string | null;
}

/**
 * A single uittreksel row as produced by the form. Every input emits a string, so indicatieGratis
 * is a boolean-as-string here and is converted before the configuration is emitted.
 */
interface UittrekselaanvraagGegevenFormValue {
  burgerservicenummer?: string;
  uittrekselcode?: string;
  indicatieGratis?: string;
}

/**
 * Raw form value of the piv-uittreksel-aanvraag configuration form. Fields are optional because
 * the form emits before it has been filled in.
 */
interface PivUittrekselAanvraagFormValue {
  zaakId?: string;
  datumAanvraag?: string;
  aanvraaggegevens?: Array<UittrekselaanvraagGegevenFormValue>;
  emailadres?: string;
  telefoonnummerPrive?: string;
  telefoonnummerWerk?: string;
  telefoonnummerMobiel?: string;
}

export {
  PivAdapterPluginConfig,
  PivUittrekselAanvraagConfig,
  PivUittrekselAanvraagFormValue,
  UittrekselaanvraagGegeven,
  UittrekselaanvraagGegevenFormValue,
};