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
	pedido_estado varchar check (pedido_estado in ('pendiente, bloqueado', 'entregado')),
	
	foreign key (producto_id) references productos(codigo),
	foreign key (cliente_id) references Clientes(identificacion)
)


CREATE OR REPLACE PROCEDURE verificar_stock(
p_producto_id varchar,
p_cantidad_compra integer
)
LANGUAGE plpgsql
AS $$
DECLARE
		v_stock_producto integer;
BEGIN
	SELECT stock INTO v_stock_producto FROM productos WHERE codigo = p_producto_id;
	if v_stock_producto >= p_cantidad_compra then
		raise notice 'el stock es suficiente, stock: % / pedido: %', v_stock_producto, p_cantidad_compra;
	else
		raise notice 'el stock es insuficiente, stock: % / pedido: %', v_stock_producto, p_cantidad_compra;
	end if;
END;
$$;

insert into productos (codigo, nombre, stock, valor_unitario) values ('1', 'pc', 50, 799.99)

call verificar_stock('1', 51)













