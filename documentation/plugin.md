# Plugin Documentation

<!-- Use this page to document your plugin. Below is a suggested structure. -->

## Overview

This plugin exposes the PIV Adapter API, a REST interface for sending requests for uittreksels, a
binnengemeentelijke verhuizing or a buitengemeentelijke verhuizing to PIV.

## Dependencies

### Backend

```kotlin
dependencies {
    implementation("com.ritense.valtimoplugins:piv-adapter-plugin:0.0.1")
}
```

### Frontend

```json
{
  "dependencies": {
    "@valtimo-plugins/piv-adapter-plugin": "0.0.1"
  }
}
```

In your `app.module.ts`:

```typescript
import {
    PivAdapterPluginModule, pivAdapterPluginSpecification,
} from '@valtimo-plugins/piv-adapter-plugin';

@NgModule({
    imports: [
        PivAdapterPluginModule,
    ],
    providers: [
        {
            provide: PLUGIN_TOKEN,
            useValue: [
                pivAdapterPluginSpecification,
            ]
        }
    ]
})
```

## Configuration

List the plugin configuration properties and how to set them.

| Property          | Type   | Required | Description                                                                              |
|-------------------|--------|----------|------------------------------------------------------------------------------------------|
| pivAdapterBaseUrl | string | Yes      | Base URL of the PIV Adapter API, including its servlet path, e.g. `https://piv.example.nl/esuite/pivadapter` |

## Actions

Each action maps onto one endpoint of `piv-adapter-openapi.yaml`.

| Action key                     | Endpoint                        | Description                                       |
|--------------------------------|---------------------------------|---------------------------------------------------|
| `piv-uittreksel-aanvraag`      | `POST /v1/uittrekselAanvraag`   | Requests one or more uittreksels                  |
| `piv-binnen-verhuis-aanvraag`  | `POST /v1/binnenVerhuisAanvraag`| Reports a move within the same municipality       |
| `piv-buiten-verhuis-aanvraag`  | `POST /v1/buitenVerhuisAanvraag`| Reports a move to another municipality            |
| `piv-status`                   | `GET /v1/status`                | Checks whether the endpoint is reachable          |

The request payloads are configured field by field, so each value can be filled from a process
variable or the case document (`pv:` / `doc:`). Values nested inside the list properties
(`aanvraaggegevens`, `meeverhuizerBsns`, `buitengemeentelijkeMeeverhuizers`) are static configuration
and are not placeholder-resolved.

`piv-status` stores the reported `application` and `version` as a map in the process variable named by
its optional `resultProcessVariable` property, which defaults to `pivAdapterStatus`.

## Usage

Explain how to use the plugin in a process, with examples if applicable.
