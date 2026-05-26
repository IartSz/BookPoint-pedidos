# Microservicio de Pedidos

## server.port=8081

## Endpoints

### GET `/api/v1/pedidos`
Lista todos los pedidos registrados.

### GET `/api/v1/direcciones`
Lista todas las direcciones registradas.
---
### GET `/api/v1/pedidos/{id}`
Obtiene un registro de pedido por su id.
---
### GET `/api/v1/direcciones/{id}`
Obtiene un registro de direccion por su id.
---
### POST `/api/v1/direcciones`
**JSON de entrada:**
```json
{
  "idUsuario": 2,
  "calle": "Av.Siempre Tuki",
  "numero": "742",
  "region": "RM",
  "ciudad": "Santiago",
  "comuna": "Providencia",
  "codigoPostal": 7500000
}
```
PARA CREAR UN CARRO
### POST `/api/v1/carrito` 
```json
{
  "idUsuario": 1,
  "tipoEntrega": "ENVIO",
  "idDireccion": 1,
  "codigoCupon": "DESC10",
  "items": [
    {
      "idProducto": 7,
      "cantidad": 1
    },
    {
      "idProducto": 5,
      "cantidad": 2
    }
  ]
}

```
### POST `/api/v1/pedidos/checkout/{idCarrito}` 
BODY VACIO CON SOLO PONER idCarrito se rellena todo.
```
{
    "idPedido": 29,
    "mensajeConfirmacion": "Gracias por tu compra, Matias Duran. Tu pedido fue recibido correctamente.",
    "cliente": "Matias Duran",
    "fecha": "2026-05-25",
    "tipoEntrega": "ENVIO",
    "direccionEnvio": "Av. Siempre Viva 742, Providencia, Santiago",
    "subtotal": 95000.0,
    "codigoCupon": "DESC10",
    "total": 85500,
    "resumenCompra": "Pedido #29\nCliente: Matias Duran\nEntrega: Av. Siempre Viva 742, Providencia, Santiago\nSubtotal: $95000.0\nCupón: DESC10\nTotal: $85500",
    "items": [
        {
            "idItemPedido": 44,
            "producto": "Harry Potter y la piedra filosofal",
            "cantidad": 1,
            "precioUnitario": 25000.0,
            "subtotal": 25000.0
        },
        {
            "idItemPedido": 45,
            "producto": "Dune",
            "cantidad": 2,
            "precioUnitario": 35000.0,
            "subtotal": 70000.0
        }
    ]
}
```

---

### DELETE `/api/v1/pedido/{id}`
Elimina un pedido por su id.

---

## Dependencias
| MS | Puerto | Para qué |
|---|---|---|
| MS Carrito | 8082 | Obtener los productos que el usuario quiere comprar|
| MS Usuario | 8083 | Obtener datos del cliente |
| MS Catalogo | 8090 | Obtener nombre y precio de productos |
| MS Inventario| 8091 | Descontar stock al confirmar la compra |
| MS Cupones | 8085 | Valida y aplicar descuentos | (OPCIONAL)
| MS Direcciones | 8081 | Obtener dirección de envio |
