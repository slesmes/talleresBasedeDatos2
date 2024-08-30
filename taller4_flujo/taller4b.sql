create table Clientes(
	identificacion varchar primary key,
	nombre varchar not null,
	edad integer not null,
	correo varchar
);

create table productos(
	codigo varchar primary key,
	nombre varchar not null,
	stock integer not null,
	valor_unitario float not null
);

create table facturas (
	id varchar primary key,
	fecha date not null,
	cantidad integer not null,  
	valor_total integer not null,
	producto_id varchar, 
	cliente_id  varchar,
	pedido_estado varchar check (pedido_estado in ('pendiente', 'bloqueado', 'entregado')),
	
	foreign key (producto_id) references productos(codigo),
	foreign key (cliente_id) references Clientes(identificacion)
)


CREATE OR REPLACE PROCEDURE actualizar_estado_pedido(
p_factura_id varchar,
p_nuevo_estado varchar
)
LANGUAGE plpgsql
AS $$
DECLARE
		v_factura_estado varchar;
BEGIN
	SELECT pedido_estado INTO v_factura_estado FROM facturas WHERE id = p_factura_id;
	if v_factura_estado = 'entregado' then
		raise notice 'el pedido ya fue entregado';
	else
		raise notice 'estado del pedido actualizado a: % / el estado anterior era: %', p_nuevo_estado, v_factura_estado;
		update facturas set pedido_estado = p_nuevo_estado where id = p_factura_id;
	end if;
END;
$$;


insert into Clientes (identificacion, nombre, edad, correo) values ('1', 'juan perez', 30, 'juan.perez@example.com');               
insert into productos (codigo, nombre, stock, valor_unitario) values ('1', 'pc', 50, 799.99);
insert into facturas (id, fecha, cantidad, valor_total, producto_id, cliente_id, pedido_estado) values ('1', '2024-08-01', 2, 1599.98,'1', '1','pendiente');          

call actualizar_estado_pedido('1', 'pendiente')

