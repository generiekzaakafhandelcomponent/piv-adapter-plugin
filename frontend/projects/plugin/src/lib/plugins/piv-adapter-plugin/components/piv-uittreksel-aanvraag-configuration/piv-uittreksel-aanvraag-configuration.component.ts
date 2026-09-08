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

import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from "@angular/core";
import {
  FunctionConfigurationComponent,
  FunctionConfigurationData,
  PluginTranslationService,
} from "@valtimo/plugin";
import {BehaviorSubject, combineLatest, map, Observable, of, Subscription, switchMap, take} from "rxjs";
import {
  PivUittrekselAanvraagConfig,
  PivUittrekselAanvraagFormValue,
  UittrekselaanvraagGegeven,
  UittrekselaanvraagGegevenFormValue,
} from "../../models";

@Component({
  standalone: false,
  selector: "valtimo-piv-uittreksel-aanvraag-configuration",
  templateUrl: "./piv-uittreksel-aanvraag-configuration.component.html",
})
export class PivUittrekselAanvraagConfigurationComponent implements FunctionConfigurationComponent, OnInit, OnDestroy {
  @Input() save$!: Observable<void>;
  @Input() disabled$!: Observable<boolean>;
  @Input() pluginId!: string;
  @Input() prefillConfiguration$!: Observable<PivUittrekselAanvraagConfig>;
  @Output() valid: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() configuration: EventEmitter<FunctionConfigurationData> = new EventEmitter<FunctionConfigurationData>();

  public indicatieGratisItems$!: Observable<Array<{id: string; text: string}>>;
  public prefillAanvraaggegevens$!: Observable<Array<UittrekselaanvraagGegevenFormValue> | undefined>;

  private saveSubscription!: Subscription;
  private readonly formValue$ = new BehaviorSubject<PivUittrekselAanvraagFormValue | null>(null);
  private readonly valid$ = new BehaviorSubject<boolean>(false);

  constructor(private readonly pluginTranslationService: PluginTranslationService) {
  }

  public ngOnInit(): void {
    this.indicatieGratisItems$ = combineLatest([
      this.pluginTranslationService.translate("indicatieGratisJa", this.pluginId),
      this.pluginTranslationService.translate("indicatieGratisNee", this.pluginId),
    ]).pipe(
      map(([ja, nee]) => [
        {id: "true", text: ja},
        {id: "false", text: nee},
      ])
    );
    this.prefillAanvraaggegevens$ = (this.prefillConfiguration$ ?? of(null)).pipe(
      map(prefill => this.toFormRows(prefill?.aanvraaggegevens))
    );
    this.openSaveSubscription();
  }

  public ngOnDestroy() {
    this.saveSubscription?.unsubscribe();
  }

  public formValueChange(formValue: PivUittrekselAanvraagFormValue): void {
    this.formValue$.next(formValue);
    this.handleValid(formValue);
  }

  private handleValid(formValue: PivUittrekselAanvraagFormValue): void {
    const aanvraaggegevens = formValue.aanvraaggegevens ?? [];
    const valid = !!(
      formValue.zaakId &&
      formValue.datumAanvraag &&
      formValue.emailadres &&
      formValue.telefoonnummerPrive &&
      aanvraaggegevens.length > 0 &&
      aanvraaggegevens.every(gegeven => !!gegeven.burgerservicenummer && !!gegeven.uittrekselcode)
    );
    this.valid$.next(valid);
    this.valid.emit(valid);
  }

  private openSaveSubscription(): void {
    this.saveSubscription = this.save$
      ?.pipe(
        switchMap(() => combineLatest([this.formValue$, this.valid$]).pipe(take(1)))
      )
      .subscribe(([formValue, valid]) => {
        if (valid) {
          this.configuration.emit(this.toConfiguration(formValue!));
        }
      });
  }

  /**
   * The form only produces strings. Convert indicatieGratis back to a boolean and turn the empty
   * optional phone numbers into null, so the backend receives the types its action expects.
   */
  private toConfiguration(formValue: PivUittrekselAanvraagFormValue): PivUittrekselAanvraagConfig {
    return {
      zaakId: formValue.zaakId!,
      datumAanvraag: formValue.datumAanvraag!,
      aanvraaggegevens: (formValue.aanvraaggegevens ?? []).map(row => {
        const gegeven: UittrekselaanvraagGegeven = {
          burgerservicenummer: row.burgerservicenummer!,
          uittrekselcode: row.uittrekselcode!,
        };
        if (row.indicatieGratis) {
          gegeven.indicatieGratis = row.indicatieGratis === "true";
        }
        return gegeven;
      }),
      emailadres: formValue.emailadres!,
      telefoonnummerPrive: formValue.telefoonnummerPrive!,
      telefoonnummerWerk: formValue.telefoonnummerWerk || null,
      telefoonnummerMobiel: formValue.telefoonnummerMobiel || null,
    };
  }

  /**
   * Maps a stored configuration onto the row shape the multi input form prefills with. Returns
   * undefined when there is nothing to prefill, so the form falls back to a single empty row.
   */
  private toFormRows(
    aanvraaggegevens?: Array<UittrekselaanvraagGegeven>
  ): Array<UittrekselaanvraagGegevenFormValue> | undefined {
    if (!aanvraaggegevens?.length) {
      return undefined;
    }
    return aanvraaggegevens.map(gegeven => ({
      burgerservicenummer: gegeven.burgerservicenummer ?? "",
      uittrekselcode: gegeven.uittrekselcode ?? "",
      indicatieGratis: gegeven.indicatieGratis === undefined || gegeven.indicatieGratis === null
        ? ""
        : String(gegeven.indicatieGratis),
    }));
  }
}