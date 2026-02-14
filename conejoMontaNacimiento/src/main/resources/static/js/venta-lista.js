///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//MOSTRAR DETALLE DE LA VENTA
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function formatearFecha(fechaISO){
    if(!fechaISO) return '';
    return new Intl.DateTimeFormat('es-MX', {
        day: '2-digit',
        month: 'long',
        year: 'numeric'
    })
    .format(new Date(fechaISO))
    .replace(/ de /g, ' ');
}

function mostrarDetalleVenta(row){
    const ventaId = row.dataset.id;
    const nombre = row.dataset.nombre;

    document.getElementById("modalDetalleTitle").textContent = `Detalle - ${nombre}`;
    
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
                    <h5 class="fw-bold mb-3 text-center">Artículos</h5>
                    <div class="row g-3 mb-4 justify-content-center"> 
                `;

                detalle.articulos.forEach(art => {
                    html += `
                        <div class="col-6 col-sm-6 col-md-4 col-lg-3">
                            <div class="card h-100 text-center">
                                <a href="${art.secureUrl}" target="_blank">
                                    <img src="${art.secureUrl}"
                                        class="card-img-top"
                                        style="height:150px; object-fit:cover;">
                                </a>

                                <div class="card-body">
                                    <h6 class="card-title">${art.nombre}</h6>
                                    <span class="badge bg-primary">
                                        Cantidad: ${art.cantidad} ${art.presentacion}
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
                    <h5 class="fw-bold mb-3 text-center">Ejemplares</h5>
                    <div class="row g-3 justify-content-center">
                `;

                detalle.ejemplares.forEach((ej, indexEj) => {
                    // Tomamos la primera imagen si existe, o una placeholder
                    const imagenes = (ej.imagenes && ej.imagenes.length > 0) ? ej.imagenes : [{ secureUrl: '/img/placeholder.png' }];
                    const carouselId = `carouselEj${indexEj}`; // ID único para cada carrusel

                    // Carrusel de imágenes
                    let carouselInner = '';
                    imagenes.forEach((img, idx) => {
                        carouselInner += `
                            <div class="carousel-item ${idx === 0 ? 'active' : ''}">
                                <a href="${img.secureUrl}" target="_blank">
                                    <img src="${img.secureUrl}" class="d-block w-100" style="height:150px; object-fit:cover;">
                                </a>
                            </div>
                        `; 
                    });

                    html += `
                        <div class="col-6 col-sm-6 col-md-4 col-lg-3">
                            <div class="card h-100 text-center">

                                <div id="${carouselId}" class="carousel slide" data-bs-ride="carousel">
                                    <div class="carousel-inner">
                                        ${carouselInner}
                                    </div>
                                    ${imagenes.length > 1 ? `
                                    <button class="carousel-control-prev" type="button" data-bs-target="#${carouselId}" data-bs-slide="prev">
                                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                        <span class="visually-hidden">Previous</span>
                                    </button>
                                    <button class="carousel-control-next" type="button" data-bs-target="#${carouselId}" data-bs-slide="next">
                                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                        <span class="visually-hidden">Next</span>
                                    </button>` : ''}
                                </div>

                                <div class="card-body">
                                    <h6 class="card-title">${ej.sexo || '-'}</h6>
                                    <span class="badge bg-secondary">Nac: ${formatearFecha(ej.fechaNacimiento) || '-'}</span>
                                    <br>
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