/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.examen;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

/**
 *
 * @author santiago
 */
public class Examen {

    private static final String uri = "mongodb://localhost:27017";
    private static final MongoClient mongoClient = MongoClients.create(uri);
    private static final MongoDatabase database = mongoClient.getDatabase("examen");
    private static final MongoCollection<Document> productosCollection = database.getCollection("productos");
    private static final MongoCollection<Document> pedidosCollection = database.getCollection("pedidos");
    private static final MongoCollection<Document> detallesCollection = database.getCollection("detalles");
    private static final MongoCollection<Document> reservasCollection = database.getCollection("reservas");

    public static void main(String[] args) {
        System.out.println("Conexión exitosa");

        // Insertar productos
        insertarProducto(1, "Camiseta", "Camiseta de algodon", 25, 100);
        insertarProducto(2, "Pantalon", "Pantalón de jean", 30, 50);
        insertarProducto(3, "Calcetines", "Calcetines deportivos", 10, 200);

        // Consultar productos
        consultarProductos();

        // Actualizar un producto
        actualizarProducto(1, "Precio", 28);
        System.out.println("Despues de actualizar el precio del producto 1:");
        consultarProductos();

        // Obtener productos con precio mayor a 20
        obtenerProductosConPrecioMayorA20();

        // Eliminar un producto
        eliminarProducto(3);
        System.out.println("Despues de eliminar el producto 3:");
        consultarProductos();

        // Insertar pedidos
        insertar_Pedido(1, "Juan Perez", "02-12-2024", "Enviado", 150.5);
        insertar_Pedido(2, "Maria López", "01-11-2024", "En proceso", 75.0);

        // Consultar pedidos
        consultarPedidos();

        // Actualizar un pedido
        actualizarPedido(2, "Estado", "Completado");
        System.out.println("Despues de actualizar el estado del pedido 2:");
        consultarPedidos();

        // Obtener pedidos con total mayor a 100
        obtenerPedidosConTotalMayorA100();

        // Eliminar un pedido
        eliminarPedido(2);
        System.out.println("Despues de eliminar el pedido 2:");
        consultarPedidos();

        // Insertar detalles
        insertar_Detalle(1, "1", "1", 2, 28.0);
        insertar_Detalle(2, "1", "2", 1, 30.0);

        // Consultar detalles
        consultarDetalles();

        // Actualizar un detalle
        actualizarDetalle(2, "Cantidad", 3);
        System.out.println("Despues de actualizar la cantidad del detalle 2:");
        consultarDetalles();

        // Eliminar un detalle
        eliminarDetalle(1);
        System.out.println("Despues de eliminar el detalle 1:");
        consultarDetalles();

        // Obtener pedidos que contienen un producto específico
        System.out.println("Pedidos que contienen el producto con ID 1:");
        obtenerPedidosConProducto(1);

        // Insertar reservas
        insertarReserva("reserva001", "Ana Gomez", "ana.gomez@example.com", "+54111223344", "Calle Ficticia 123, Buenos Aires, Argentina",
                "Suite", 101, 200.00, 2, "Suite con vista al mar, cama king size, baño privado y balcón.",
                "2024-12-15T14:00:00Z", "2024-12-18T12:00:00Z", 740.00, "Pagado", "Tarjeta de Crédito", "2024-11-30T10:00:00Z");

        // Consultar reservas
        consultarReservas();

        // Actualizar reserva
        actualizarReserva("reserva001", "estado_pago", "Pendiente");
        System.out.println("Despues de actualizar el estado de pago:");
        consultarReservas();

        //consultas
        obtenerHabitacionesSencillas();
        obtenerSumatoriaReservasPagadas();
        obtenerReservasConPrecioNocheMayorA100();
        
        // Eliminar reserva
        eliminarReserva("reserva001");
        System.out.println("Despues de eliminar la reserva:");
        consultarReservas();
    }

//    crud productos
    public static void insertarProducto(int id, String nombre, String descripcion, double precio, double stock) {
        Document producto = new Document("Tipo", "Producto")
                .append("ProductoID", id)
                .append("Nombre", nombre)
                .append("Descripcion", descripcion)
                .append("Precio", precio)
                .append("Stock", stock);
        productosCollection.insertOne(producto);
    }

    public static void consultarProductos() {
        System.out.println("\nLista de productos:");
        for (Document producto : productosCollection.find(eq("Tipo", "Producto"))) {
            System.out.println(producto.toJson());
        }
    }

    public static void actualizarProducto(int id, String campo, Object nuevoValor) {
        productosCollection.updateOne(eq("ProductoID", id), new Document("$set", new Document(campo, nuevoValor)));
        System.out.println("Producto actualizado correctamente.");
    }

    public static void eliminarProducto(int id) {
        productosCollection.deleteOne(eq("ProductoID", id));
        System.out.println("Producto eliminado correctamente.");
    }

    public static void obtenerProductosConPrecioMayorA20() {
        System.out.println("\nProductos con precio mayor a 20 dólares:");
        for (Document producto : productosCollection.find(gt("Precio", 20))) {
            System.out.println(producto.toJson());
        }
    }

//    crud pedido
    public static void insertar_Pedido(int id, String cliente, String fecha_pedido, String estado, double total) {
        Document pedido = new Document("Tipo", "Pedido")
                .append("PedidoID", id)
                .append("Cliente", cliente)
                .append("Fecha_pedido", fecha_pedido)
                .append("Estado", estado)
                .append("Total", total);
        pedidosCollection.insertOne(pedido);
    }

    public static void consultarPedidos() {
        System.out.println("\nLista de pedidos:");
        for (Document pedido : pedidosCollection.find(eq("Tipo", "Pedido"))) {
            System.out.println(pedido.toJson());
        }
    }

    public static void actualizarPedido(int id, String campo, Object nuevoValor) {
        pedidosCollection.updateOne(eq("PedidoID", id), new Document("$set", new Document(campo, nuevoValor)));
        System.out.println("Pedido actualizado correctamente.");
    }

    public static void eliminarPedido(int id) {
        pedidosCollection.deleteOne(eq("PedidoID", id));
        System.out.println("Pedido eliminado correctamente.");
    }

    public static void obtenerPedidosConTotalMayorA100() {
        System.out.println("\nPedidos con total mayor a dólares:");
        for (Document pedido : pedidosCollection.find(gt("Total", 100))) {
            System.out.println(pedido.toJson());
        }
    }

    public static void obtenerPedidosConProducto(int productoId) {
        System.out.println("\nPedidos que contienen el producto con ID: " + productoId);

        List<String> pedidosIds = new ArrayList<>();
        for (Document detalle : detallesCollection.find(eq("Producto_id", productoId))) {
            pedidosIds.add(detalle.getString("Pedido_id"));
        }

        for (String pedidoId : pedidosIds) {
            Document pedido = pedidosCollection.find(eq("PedidoID", pedidoId)).first();
            if (pedido != null) {
                System.out.println(pedido.toJson());
            }
        }
    }

    // crud detalle
    public static void insertar_Detalle(int id, String pedido_id, String producto_id, int cantidad, double precio_unitario) {
        Document detalle = new Document("Tipo", "Detalle")
                .append("DetalleID", id)
                .append("Pedido_id", pedido_id)
                .append("Producto_id", producto_id)
                .append("Cantidad", cantidad)
                .append("Precio_unitario", precio_unitario);
        detallesCollection.insertOne(detalle);
    }

    public static void consultarDetalles() {
        System.out.println("\nLista de detalles:");
        for (Document detalle : detallesCollection.find(eq("Tipo", "Detalle"))) {
            System.out.println(detalle.toJson());
        }
    }

    public static void actualizarDetalle(int id, String campo, Object nuevoValor) {
        detallesCollection.updateOne(eq("DetalleID", id), new Document("$set", new Document(campo, nuevoValor)));
        System.out.println("Detalle actualizado correctamente.");
    }

    public static void eliminarDetalle(int id) {
        detallesCollection.deleteOne(eq("DetalleID", id));
        System.out.println("Detalle eliminado correctamente.");
    }

    //crud reservas
    public static void insertarReserva(String id, String nombre, String correo, String telefono, String direccion,
            String tipoHabitacion, int numeroHabitacion, double precioNoche, int capacidad, String descripcion,
            String fechaEntrada, String fechaSalida, double total, String estadoPago, String metodoPago, String fechaReserva) {
        Document cliente = new Document("nombre", nombre)
                .append("correo", correo)
                .append("telefono", telefono)
                .append("direccion", direccion);

        Document habitacion = new Document("tipo", tipoHabitacion)
                .append("numero", numeroHabitacion)
                .append("precio_noche", precioNoche)
                .append("capacidad", capacidad)
                .append("descripcion", descripcion);

        Document reserva = new Document("_id", id)
                .append("cliente", cliente)
                .append("habitacion", habitacion)
                .append("fecha_entrada", fechaEntrada)
                .append("fecha_salida", fechaSalida)
                .append("total", total)
                .append("estado_pago", estadoPago)
                .append("metodo_pago", metodoPago)
                .append("fecha_reserva", fechaReserva);

        reservasCollection.insertOne(reserva);
        System.out.println("Reserva insertada correctamente.");
    }

    public static void consultarReservas() {
        System.out.println("\nLista de reservas:");
        for (Document reserva : reservasCollection.find()) {
            System.out.println(reserva.toJson());
        }
    }

    public static void actualizarReserva(String id, String campo, Object nuevoValor) {
        reservasCollection.updateOne(eq("_id", id), new Document("$set", new Document(campo, nuevoValor)));
        System.out.println("Reserva actualizada correctamente.");
    }

    public static void eliminarReserva(String id) {
        reservasCollection.deleteOne(eq("_id", id));
        System.out.println("Reserva eliminada correctamente.");
    }

    public static void obtenerHabitacionesSencillas() {
        System.out.println("\nHabitaciones reservadas de tipo 'Sencilla':");
        for (Document reserva : reservasCollection.find(eq("habitacion.tipo", "Sencilla"))) {
            System.out.println("Reserva ID: " + reserva.getString("_id"));
            System.out.println("Cliente: " + ((Document) reserva.get("cliente")).getString("nombre"));
            System.out.println("Habitación: " + ((Document) reserva.get("habitacion")).toJson());
            System.out.println("-------------------------------");
        }
    }

    public static void obtenerSumatoriaReservasPagadas() {
        double sumatoria = 0;

        for (Document reserva : reservasCollection.find(eq("estado_pago", "Pagado"))) {
            sumatoria += reserva.getDouble("total");
        }

        System.out.println("\nLa sumatoria total de las reservas pagadas es: $" + sumatoria);
    }

    public static void obtenerReservasConPrecioNocheMayorA100() {
        System.out.println("\nReservas de habitaciones con precio por noche mayor a $100:");
        for (Document reserva : reservasCollection.find(gt("habitacion.precio_noche", 100))) {
            System.out.println("Reserva ID: " + reserva.getString("_id"));
            System.out.println("Cliente: " + ((Document) reserva.get("cliente")).getString("nombre"));
            System.out.println("Habitacion: " + ((Document) reserva.get("habitacion")).toJson());
            System.out.println("Total de la reserva: $" + reserva.getDouble("total"));
            System.out.println("-------------------------------");
        }
    }

}
