Assessment Intergamma
=====================

This codebase represents an assessment for Intergamma. It is a Stock management and reservation application.

# Structure

The service enables managing `StockItem`s. `StockItem`s are unique items associated with a given product, that can be independently added, removed, reserved and updated (the store where they are located).

# API

Here we provide an overview of the API of the service

## Product API

### GET /product/{productCode}
Retrieves the total availability and reservations of `StockItem`s for a given product.

### POST /product/{productCode}
Increment stock for a given product:

```json
{
	"storeCode": "someStoreCode",
	"quantity": 1
}
```

## StockItem API

### GET /stockitem?product={productCode}

Retrieves all the `StockItem`s that carry the given product code.

### DELETE /stockitem/{stockItemId}

Hard removes a given `StockItem`. No business rules have been applied such as failure to remove if a reservation exists, as no such rules have been given.

### PATCH /stockitem/{stockItemId}

Allows setting the `storeCode` and/or `reserved` properties of a `StockItem`

```json
{
	"storeCode": "updated-store-code",
	"reserved": true
}
```

If `reserved` is set, its reservation timestamp is set to the moment of receipt. It will be pruned after a configurable amount of time. This is currently configured in application.properties to 30 minutes.