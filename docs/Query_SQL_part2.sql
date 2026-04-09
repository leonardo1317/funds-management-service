--Obtener los nombres de los clientes que tienen inscrito algún producto disponible solo en las sucursales que visitan.
SELECT DISTINCT
    c.nombre,
    c.apellidos
FROM cliente c
JOIN inscripcion i ON c.id = i.idCliente
JOIN disponibilidad d ON i.idProducto = d.idProducto
LEFT JOIN visitan v 
    ON v.idCliente = c.id 
   AND v.idSucursal = d.idSucursal
GROUP BY c.id, c.nombre, c.apellidos, i.idProducto
HAVING COUNT(DISTINCT d.idSucursal) = COUNT(DISTINCT v.idSucursal);