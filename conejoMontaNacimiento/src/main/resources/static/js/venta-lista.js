///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//MOSTRAR DETALLE DE LA VENTA
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
function mostrarDetalleVenta(ventaId){
    const body = document.getElementById("modalDetalleBody");

    body.innerHTML = `
        <div class="text-center py-4">
            <div class="spinner-border text-primary"></div>
        </div>
    `;

    new bootstrap.Modal(
        document.getElementById("modalDetalle")
    ).show();

    fetch(`/api/ventas/${ventaId}`)
        .then(resp => {
            if(!resp.ok){
                throw new Error("Error al obtener detalle");
            }
            return resp.json();
        })
        .then(detalle => {
            let html = '';

            // Articulos
            if(detalle.articulos && detalle.articulos.length > 0){
                html += `
                    <h5 class="fw-bold mb-3">Artículos</h5>
                    <div class="row g-3 mb-4"> 
                `;

                detalle.articulos.forEach(art => {
                    html += `
                        <div class="col-md-4">
                            <div class="card h-100 text-center">
                                <img src="${art.secureUrl}"
                                     class="card-img-top"
                                     style="height:150px; object-fit:cover;">

                                <div class="card-body">
                                    <h6 class="card-title">${art.nombre}</h6>
                                    <span class="badge bg-primary">
                                        Cantidad: ${art.cantidad}
                                    </span>
                                </div>

                            </div>
                        </div>
                    `;
                });

                html += `</div>`;
            }

            // Ejemplares
            if(detalle.ejemplares && detalle.ejemplares.length > 0){
                html += `
                    <h5 class="fw-bold mb-3">Ejemplares</h5>
                    <div class="row g-3 mb-4">
                `;

                detalle.ejemplares.forEach(ej => {
                    // Tomamos la primera imagen si existe, o una placeholder
                    const imgUrl = (ej.imagenes && ej.imagenes.length > 0) ? ej.imagenes[0].secureUrl : '/img/placeholder.png';

                    html += `
                        <div class="col-md-4">
                            <div class="card h-100 text-center">
                                <img src="${imgUrl}"
                                    class="card-img-top"
                                    style="height:150px; object-fit:cover;">

                                <div class="card-body">
                                    <h6 class="card-title">Ejemplar</h6>
                                    <span class="badge bg-info me-1">Sexo: ${ej.sexo || '-'}</span>
                                    <span class="badge bg-secondary">Nacimiento: ${ej.fechaNacimiento || '-'}</span>
                                    <br><br>
                                    <span class="badge bg-success me-1">Padre: ${ej.padre ? ej.padre.nombre : '-'}</span>
                                    <span class="badge bg-danger">Madre: ${ej.madre ? ej.madre.nombre : '-'}</span>
                                </div>

                            </div>
                        </div>
                    `;
                });

                html += `</div>`;
            }
            
            // Sin contenido
            if(html === ''){
                html = `
                    <div class="alert alert-info text-center mb-0">
                        Esta venta no contiene artículos ni ejemplares.
                    </div>
                `;
            }

            body.innerHTML = html;
        })
        .catch((error) => {
            console.error("Error al cargar el detalle de la venta:", error)

            body.innerHTML = `
                <div class="alert alert-danger text-center">
                    Error al cargar el detalle de la venta
                </div>
            `;
        });
}