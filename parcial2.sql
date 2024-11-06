create table Usuarios (
	id serial primary key,
	nombre varchar,
	direccion varchar,
	email varchar,
	fecha_registro date,
	estado varchar check (estado in ('activo','inactivo') )
);

create table tarjetas (
	id serial primary key,
	numero_tarjeta varchar,
	fecha_expiracion date,
	cvv varchar,
	tipo_tarjeta varchar check(tipo_tarjeta in ('Visa', 'Mastercard'))
);

create table productos (
	id serial primary key,
	codigo_producto varchar,
	nombre varchar,
	categoria varchar check(categoria in ('celular','pc','televisor')),
	porcentaje_impuesto numeric,
	precio numeric
);

create table pagos (
	id serial primary key,
	codigo_pago varchar,
	fecha date,
	estado varchar check (estado in ('exitoso', 'fallido')),
	monto numeric,
	producto_id int,
	tarjeta_id int,
	usuario_id int,
	
	foreign key(producto_id) references productos(id),
	foreign key(tarjeta_id) references tarjetas(id),
	foreign key(usuario_id) references Usuarios(id)
);

create table comprobante (
	id serial primary key,
	detalle_xml xml,
	detalle_json json
)


create or replace function pagos_usuario(usuario_id_v int, fecha_v date)
returns table (
	codigo_pago_v varchar,
	nombre_producto_v varchar,
	monto_v numeric,
	estado_v varchar
)
language plpgsql
as $$
begin
	return query select p.codigo_pago, r.nombre, p.monto, p.estado from pagos p join productos on p.producto_id = r.id where usuario_id_v = p.usuario_id and fecha_v = p.fecha;                       
end;
$$;

-----fin primera pregunta----------------------------------

create OR replace procedure obtener_tarjetas_detalles_usuario (usuario_id_p INT)
language plpgsql
as $$
declare
    numero_tarjeta_v varchar;
    fecha_expiracion_v date;
    nombre_v varchar;
    email_v varchar;
    envio_cursor cursor for
        select t.numero_tarjeta, t.fecha_expiracion, u.nombre, u.email 
        from tarjetas t 
        join Usuarios u ON t.usuario_id = u.id
        where u.id = usuario_id_p;
begin
    open envio_cursor;
    loop
        fetch envio_cursor into numero_tarjeta_v, fecha_expiracion_v, nombre_v, email_v;
        exit when not found;
        raise notice 'numero tarjeta: %', numero_tarjeta_v;
        raise notice 'fecha expiracion: %', fecha_expiracion_v;
        raise notice 'nombre: %', nombre_v;
        raise notice 'email: %', email_v;
    end loop;
    close envio_cursor;
end;
$$;


CREATE OR REPLACE PROCEDURE guardar_xml(
    codigo_pago_p VARCHAR,
    nombre_usuario_p VARCHAR,
    numero_tarjeta_p VARCHAR,
    nombre_producto_p VARCHAR,
    monto_pago_p NUMERIC
)
LANGUAGE plpgsql
AS $$
DECLARE
    xml_data XML;
BEGIN
    xml_data := XMLFORMAT(
        '<pago>
            <codigo_pago>' || codigo_pago_p || '</codigo_pago>
            <nombre_usuario>' || nombre_usuario_p || '</nombre_usuario>
            <numero_tarjeta>' || numero_tarjeta_p || '</numero_tarjeta>
            <nombre_producto>' || nombre_producto_p || '</nombre_producto>
            <monto_pago>' || monto_pago_p || '</monto_pago>
        </pago>'
    );

    INSERT INTO comprobante (detalle_xml)
    VALUES (xml_data);

    RAISE NOTICE 'XML insertado en comprobante: %', xml_data;
END;
$$;

CREATE OR REPLACE PROCEDURE guardar_json(
    email_usuario_p VARCHAR,
    numero_tarjeta_p VARCHAR,
    tipo_tarjeta_p VARCHAR,
    codigo_producto_p VARCHAR,
    codigo_pago_p VARCHAR,
    monto_pago_p NUMERIC
)
LANGUAGE plpgsql
AS $$
DECLARE
    json_data JSON;
BEGIN
    json_data := json_build_object(
        'email_usuario', email_usuario_p,
        'numero_tarjeta', numero_tarjeta_p,
        'tipo_tarjeta', tipo_tarjeta_p,
        'codigo_producto', codigo_producto_p,
        'codigo_pago', codigo_pago_p,
        'monto_pago', monto_pago_p
    );
    INSERT INTO comprobante (detalle_json)
    VALUES (json_data);

    RAISE NOTICE 'JSON insertado en comprobante: %', json_data;
END;
$$;


--fin segunda pregunta-----------------

create or replace function validar_producto() 
returns trigger as $$
begin
    if new.precio <= 0 or new.precio >= 20000 then
        raise exception 'El precio debe ser mayor a 0 y menor a 20000';
    end if;

    if new.porcentaje_impuesto < 0.01 or new.porcentaje_impuesto > 0.2 then
        raise exception 'El porcentaje de impuesto debe ser mayor o igual a 0.01 y menor o igual a 0.2';
    end if;

  	return new;
end;
$$ language plpgsql;

create trigger validaciones_producto
before insert on productos
for each row
execute function validar_producto();


CREATE OR REPLACE FUNCTION generar_comprobante_xml_json()
RETURNS TRIGGER AS $$
DECLARE
    nuevo_xml XML;
    nuevo_json JSON;
BEGIN
    nuevo_xml := XMLFORMAT(
        '<pago>
            <id>' || NEW.id || '</id>
            <codigo_pago>' || NEW.codigo_pago || '</codigo_pago>
            <fecha>' || NEW.fecha || '</fecha>
            <estado>' || NEW.estado || '</estado>
            <monto>' || NEW.monto || '</monto>
            <producto_id>' || NEW.producto_id || '</producto_id>
            <tarjeta_id>' || NEW.tarjeta_id || '</tarjeta_id>
            <usuario_id>' || NEW.usuario_id || '</usuario_id>
        </pago>'
    );

    nuevo_json := json_build_object(
        'id', NEW.id,
        'codigo_pago', NEW.codigo_pago,
        'fecha', NEW.fecha,
        'estado', NEW.estado,
        'monto', NEW.monto,
        'producto_id', NEW.producto_id,
        'tarjeta_id', NEW.tarjeta_id,
        'usuario_id', NEW.usuario_id
    );

    INSERT INTO comprobante (detalle_xml, detalle_json)
    VALUES (nuevo_xml, nuevo_json);

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_xml
AFTER INSERT ON pagos
FOR EACH ROW
EXECUTE FUNCTION generar_comprobante_xml_json();



---fin tercera pregunta------------------

create sequence codigo_producto
    start with 5
    increment by 5;
 
create sequence codigo_pagos
	start with 1
	increment by 100;

   
CREATE OR REPLACE FUNCTION obtener_info_xml(comprobante_id INT)
RETURNS TABLE (
    nombre_usuario VARCHAR,
    nombre_producto VARCHAR,
    monto_pago NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY 
    SELECT 
        xpath_text('/pago/nombre_usuario/text()', detalle_xml::xml) AS nombre_usuario,
        xpath_text('/pago/nombre_producto/text()', detalle_xml::xml) AS nombre_producto,
        xpath_text('/pago/monto_pago/text()', detalle_xml::xml)::NUMERIC AS monto_pago
    FROM comprobante
    WHERE id = comprobante_id;
END;
$$;


CREATE OR REPLACE FUNCTION obtener_info_json(comprobante_id INT)
RETURNS TABLE (
    email_usuario VARCHAR,
    codigo_producto VARCHAR,
    monto_pago NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY 
    SELECT 
        detalle_json->>'email_usuario' AS email_usuario,
        detalle_json->>'codigo_producto' AS codigo_producto,
        (detalle_json->>'monto_pago')::NUMERIC AS monto_pago
    FROM comprobante
    WHERE id = comprobante_id;
END;
$$;

-------fin cuarta pregunta-----------------------------------------------------
	
	
	













