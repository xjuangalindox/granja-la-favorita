///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//VALIDAR UPDATE (ARTICULOS Y EJEMPLARES)
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

window.addEventListener('DOMContentLoaded', () => {    
    if(articulosVenta != null){
        console.log("articulos venta existentes: ", articulosVenta);
        if(articulosVenta.length > 0){
            articulosVenta.forEach(art => agregarArticuloExistente(art));
        }
    }

    if(idsNacimientosUtilizados != null){
        console.log("ids nacimientos utilizados: ", idsNacimientosUtilizados);
        idsNacimientosUtilizados.forEach(nacimientoId => agregarNacimientosUtilizados(nacimientoId));
    }
});

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// NORMALIZAR TELEFONO
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

document.addEventListener("DOMContentLoaded", function () {

    const telefonoInput = document.getElementById("telefono");

    if (!telefonoInput) return;

    telefonoInput.addEventListener("input", function () {
        let numeros = this.value.replace(/\D/g, '');

        if (numeros.length > 10) {
            numeros = numeros.slice(-10);
        }

        this.value = numeros;
    });

});

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CONTADORES Y LISTAS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

let contArticulo = 0; // contador para cada ArticuloVenta
let contNacimiento = 0; // solo para que cada nacimiento sea único al eliminarlo
let contEjemplar = 0; // contador para cada EjemplarVenta

const articulosSeleccionados = [];
const nacimientosSeleccionados = [];

let articulosEliminados = [];
let nacimientosEliminados = [];

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CREACION Y MODIFICACION DE ARTICULOS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function guardarArticulo(articuloId) {

    // Guardar el id del articulo seleccionado
    articuloId = parseInt(articuloId);
    if (!articulosSeleccionados.includes(articuloId)) {
        articulosSeleccionados.push(articuloId);
        console.log("Artículo agregado:", articuloId);
    }
}

function liberarArticulo(articuloId) {
    const index = articulosSeleccionados.indexOf(articuloId);
    if (index !== -1) { // ¿Encontrado?
        articulosSeleccionados.splice(index, 1);
        console.log("Artículo eliminado:", articuloId);
    }
}

function eliminarArticulo(boton) {
    const row = boton.closest('tr'); // Encontramos la fila del artículo

    const select = row.querySelector('select');
    const articuloId = parseInt(select.value);
    if(articuloId){
        liberarArticulo(articuloId);
    }

    // Buscar dentro del row un input oculto cuyo name termina en .id
    const inputId = row.querySelector('input[type="hidden"][name$=".id"]');
    if(inputId && inputId.value){ // Si encontró el input y tiene valor
        articulosEliminados.push(parseInt(inputId.value));
        console.log("ArticuloVenta eliminado: "+inputId.value)
    }

    row.remove();
    actualizarTotalVenta()
}

//*************************************************************************************************************************

function agregarArticulo() {
    const tbody = document.getElementById('articulosContainer');

    // Generamos las opciones del select en JS
    let opciones = '<option value="">Selecciona un artículo</option>';

    listaArticulos.forEach(art => {
        if (!articulosSeleccionados.includes(art.id)) {
            opciones += `<option value="${art.id}" data-precio="${art.precio}">
                            ${art.presentacion} ${art.nombre}  ($${art.precio})
                        </option>`;
        }
    });

    const row = document.createElement('tr');
    row.innerHTML = `
        <td>
            <select name="articulosVenta[${contArticulo}].articulo.id" class="form-select" required
                onchange="guardarArticulo(this.value)">
                ${opciones}
            </select>
        </td>
        <td>
            <input type="number" name="articulosVenta[${contArticulo}].cantidad" class="form-control" min="1"
                required oninput="actualizarSubtotal(this)">
        </td>
        <td>
            <input type="number" name="articulosVenta[${contArticulo}].subtotal" class="form-control"
                readonly required>
        </td>

        <td>
            <button type="button" class="btn btn-danger btn-sm" onclick="eliminarArticulo(this)">
                <i class="bi bi-x-lg"></i>
            </button>
        </td>
    `;
     
    tbody.appendChild(row);
    contArticulo++;
}

//*************************************************************************************************************************

function agregarArticuloExistente(art) {
    const tbody = document.getElementById('articulosContainer');

    // Generamos las opciones del select en JS
    let opciones = '<option value="">Selecciona un artículo</option>';

    listaArticulos.forEach(item => {
        if (!articulosSeleccionados.includes(item.id)) {
            const selected = item.id === art.articulo.id ? 'selected' : '';
            opciones += `<option value="${item.id}" data-precio="${item.precio}" ${selected}>
                            ${item.presentacion} ${item.nombre}  ($${item.precio})
                        </option>`;
        }
    });

    const row = document.createElement('tr');
    row.innerHTML = `
        <!-- Campo oculto para el id del articulo -->
        <input type="hidden" name="articulosVenta[${contArticulo}].id" value="${art.id}"/>

        <td>
            <select name="articulosVenta[${contArticulo}].articulo.id" class="form-select" required
                onchange="guardarArticulo(this.value)">
                ${opciones}
            </select>
        </td>
        <td>
            <input type="number" name="articulosVenta[${contArticulo}].cantidad" class="form-control" value="${art.cantidad}" min="1" 
                required oninput="actualizarSubtotal(this)">
        </td>
        <td>
            <input type="number" name="articulosVenta[${contArticulo}].subtotal" class="form-control" value="${art.subtotal}" 
                readonly required>
        </td>

        <td>
            <button type="button" class="btn btn-danger btn-sm" onclick="eliminarArticulo(this)">
                <i class="bi bi-x-lg"></i>
            </button>
        </td>
    `;
    
    tbody.appendChild(row);
    contArticulo++;

    // guardar manualmente los ids de articulos selecionados
    const select = row.querySelector('select');
    if (select.value) {
        guardarArticulo(select.value);
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CREACION Y MODIFICACION DE EJEMPLARES
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function guardarNacimiento(nacimientoId) {
    nacimientoId = parseInt(nacimientoId);
    if(!nacimientosSeleccionados.includes(nacimientoId)){
        nacimientosSeleccionados.push(nacimientoId);
        console.log("Nacimiento agregado: ", nacimientoId);
    }
}

function liberarNacimiento(nacimientoId) {
    const index = nacimientosSeleccionados.indexOf(nacimientoId);
    if (index !== -1) { // ¿Encontrado?
        nacimientosSeleccionados.splice(index, 1);
        console.log("Nacimiento eliminado:", nacimientoId);
    }
}

function eliminarNacimiento(boton){
    const bloque = boton.closest('div.mb-4');

    const select = bloque.querySelector('select');
    const nacimientoId = parseInt(select.value);
    if(nacimientoId){
        liberarNacimiento(nacimientoId);
    }
    
    bloque.remove();
    actualizarTotalVenta();
}

function agregarNacimientoEliminado(boton){
    const bloque = boton.closest('div.mb-4');

    const select = bloque.querySelector('select');
    const nacimientoId = parseInt(select.value);
    
    if(nacimientoId){
        nacimientosEliminados.push(nacimientoId);
        console.log("Nacimiento existente eliminado: ",nacimientoId);
    }
}

// function onCheckboxChange(checkbox) {
    // actualizarTotalVenta(); // Actualizar el total de la venta
// }

//*************************************************************************************************************************

// 1. Función global para formatear opciones
function formatOption(state) {
    if (!state.id) return state.text;

    const imgMacho = $(state.element).data('img-macho');
    const imgHembra = $(state.element).data('img-hembra');
    const name = state.text;

    return $(`
        <div class="option-wrapper">
            <img src="${imgMacho}" class="img-select" title="Macho" />
            <img src="${imgHembra}" class="img-select" title="Hembra" />
            <span>&nbsp;&nbsp;${name}</span>
        </div>
    `);
}

$(document).ready(function() {
    // Inicializas Select2 para selects que ya están en el HTML al cargar la página
    $('#nacimiento-0').select2({
        templateResult: formatOption,
        templateSelection: formatOption,
        minimumResultsForSearch: -1
    });
});

function formatearFecha(fechaISO){
    if(!fechaISO) return '';
    return new Intl.DateTimeFormat('es-MX', {
        day: '2-digit',
        month: 'long',
        year: 'numeric'
    }).format(new Date(fechaISO));
}

function agregarNacimiento() {
    const contenedor = document.getElementById('nacimientosContainer');
    const nacimientoIndex = contNacimiento++;

    // Generamos las opciones del select en JS
    let opciones = '<option value="">Selecciona un nacimiento</option>';

    listaNacimientos.forEach(nac => {
        if (!nacimientosSeleccionados.includes(nac.id)) {

            const fechaFormateada = formatearFecha(nac.fechaNacimiento);
            opciones += `<option 
                            value="${nac.id}"
                            data-img-macho="${nac.monta.macho.secureUrl}"
                            data-img-hembra="${nac.monta.hembra.secureUrl}">

                            ${nac.monta.macho.nombre} - ${nac.monta.hembra.nombre} (${fechaFormateada})
                        </option>`;
        }
    });

    const bloque = document.createElement('div');
    bloque.classList.add('mb-4', 'border', 'p-3', 'rounded');
    bloque.dataset.nacimientoIndex = nacimientoIndex;

    bloque.innerHTML = `
        <div class="d-flex justify-content-between align-items-start mb-2">
            <select id="nacimiento-${nacimientoIndex}" class="form-select" required
                onchange="guardarNacimiento(this.value); 
                mostrarEjemplares(this.value, ${nacimientoIndex})">
                ${opciones}
            </select>

            <button type="button" class="btn btn-danger btn-sm mx-2" onclick="eliminarNacimiento(this)">
                <i class="bi bi-x-lg"></i>
            </button>
        </div>

        <div id="ejemplares-${nacimientoIndex}">
            <!-- Aqui van los ejemplares dinámicamente -->
        </div>
    `;

    contenedor.appendChild(bloque);

    // 3. Inicializas Select2 solo en el nuevo select creado
    $(`#nacimiento-${nacimientoIndex}`).select2({
        templateResult: formatOption,
        templateSelection: formatOption,
        minimumResultsForSearch: -1
    });
}

function toggleSeleccion(card){
    const input = card.querySelector(".vendido-input");

    if(input.value === "true"){
        input.value = "false";
        card.classList.remove("selected");
    }else{
        input.value = "true";
        card.classList.add("selected")
    }

    actualizarTotalVenta();
}

function mostrarEjemplares(nacimientoId, nacimientoIndex) {
    const nacimientoDTO = listaNacimientos.find(nac => nac.id == nacimientoId);
    if(!nacimientoDTO) return;

    const contenedor = document.getElementById(`ejemplares-${nacimientoIndex}`);
    contenedor.innerHTML = ''; // Limpiar contenido anterior

    const row = document.createElement('div');
    row.classList.add('row', 'g-3'); // g-3 para espacio entre columnas

    ///////////////////////////////////////////////////////////////////////////////
    // INICIO CAMBIOS
    ///////////////////////////////////////////////////////////////////////////////
    
    // nacimientoDTO.ejemplares.forEach(ejemplar => { // original
    nacimientoDTO.ejemplares.forEach((ejemplar, indexEj) => {
        const col = document.createElement('div');
        col.classList.add(
        "col-12",   // celular
        "col-sm-6", // tablet chica
        "col-md-4", // tablet grande
        "col-lg-3"  // desktop
        );

        const imagenes = (ejemplar.fotos && ejemplar.fotos.length > 0) ? ejemplar.fotos : [{ secureUrl: '/img/placeholder.png' }];
        const carouselId = `carouselEj${nacimientoIndex}_${indexEj}`; // ID único para cada carrucel

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

        // <div class="card h-100 text-center" onclick="toggleSeleccion(this)">
        let html = `
            <div class="card h-100 text-center">
                <div id="${carouselId}" class="carousel slide" data-bs-ride="carousel">
                    <div class="carousel-inner">
                        ${carouselInner}
                    </div>
                
                    ${imagenes.length > 1 ? 
                        `<button class="carousel-control-prev" type="button" data-bs-target="#${carouselId}" data-bs-slide="prev">
                            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                            <span class="visually-hidden">Previous</span>
                        </button>
                        <button class="carousel-control-next" type="button" data-bs-target="#${carouselId}" data-bs-slide="next">
                            <span class="carousel-control-next-icon" aria-hidden="true"></span>
                            <span class="visually-hidden">Next</span>
                        </button>` 
                        : 
                        ''}
                </div>

                <div class="card-body">
                    <h6 class="card-title">${ejemplar.sexo}</h6>
                    <span class="badge bg-success me-1">Precio: $${ejemplar.precio} MXM</span>
                    ${ejemplar.precioOferta ? `<span class="badge bg-danger">Oferta: $${ejemplar.precioOferta} MXM</span>` : ''}
                </div>

                <input type="hidden" name="ejemplaresVenta[${contEjemplar}].ejemplar.id" value="${ejemplar.id}">
                <input type="hidden" name="ejemplaresVenta[${contEjemplar}].ejemplar.precio" value="${ejemplar.precio}" class="precio-input">
                <input type="hidden" name="ejemplaresVenta[${contEjemplar}].ejemplar.precioOferta" value="${ejemplar.precioOferta}" class="oferta-input">
                <input type="hidden" name="ejemplaresVenta[${contEjemplar}].ejemplar.vendido" value="false" class="vendido-input">
                <input type="hidden" name="ejemplaresVenta[${contEjemplar}].precio" value="${ejemplar.precioOferta ? ejemplar.precioOferta : ejemplar.precio}">
            </div>
        `;

        col.innerHTML = html;

        const card = col.querySelector(".card");
        card.addEventListener("click", e => {
            // si el click fue dentro del carousel -> ignorar
            if (e.target.closest(".carousel"))
                return;

            // si el click fue fuera del carousel -> selected/unselected
            toggleSeleccion(card)
        });

        ///////////////////////////////////////////////////////////////////////////////
        // FIN CAMBIOS
        ///////////////////////////////////////////////////////////////////////////////

        // // Primera foto de cada ejemplar
        // const primeraFoto = (ejemplar.fotos && ejemplar.fotos.length > 0) ?
        //     ejemplar.fotos[0].secureUrl : 'default.jpg';

        // // Mostrar tipo de precio (normal u oferta)
        // let tipoPrecio = (ejemplar.precio != null && ejemplar.precioOferta == null) ?
        //     "Precio" : "Oferta";    

        // // Obtener precio a mostrar
        // let precio = (ejemplar.precio != null && ejemplar.precioOferta == null) ?
        //         ejemplar.precio : ejemplar.precioOferta;

        // col.innerHTML = `
        //     <div class="p-2 border rounded">
        //         <input type="hidden" name="ejemplaresVenta[${contEjemplar}].ejemplar.id" value="${ejemplar.id}">

        //         <input class="form-check-input mb-2" 
        //             type="checkbox" 
        //             name="ejemplaresVenta[${contEjemplar}].ejemplar.vendido" 
        //             onchange="onCheckboxChange(this)">

        //         <a href="${primeraFoto}"  target="_blank">
        //             <img src="${primeraFoto}"  
        //                 class="img-thumbnail vista-previa" 
        //                 style="max-width: 100px; max-height: 100px; object-fit: cover;"/>
        //         </a>

        //         <div>
        //             <p class="mb-1"><strong>Sexo: </strong>${ejemplar.sexo}</p>
        //             <p class="mb-1"><strong>${tipoPrecio}: </strong>$${precio}</p>

        //             <input type="hidden"
        //                 class="precio-input"
        //                 name="ejemplaresVenta[${contEjemplar}].precio"
        //                 value="${precio}">
        //         </div>
        //     </div>
        // `;
        row.appendChild(col);
        contEjemplar++;
    });

    contenedor.appendChild(row); // Agrega la fila completa
}

//*************************************************************************************************************************

// PRUEBA, CORRECCION DE MOSTRAR EJEMPLARES EXISTENTES
function agregarNacimientosUtilizados(nacimientoId) {
    const contenedor = document.getElementById('nacimientosContainer');
    const nacimientoIndex = contNacimiento++;

    // Obtener nacimientos utilizados
    const nac = listaNacimientos.find(n => n.id === nacimientoId);
    if(!nac) return;

    // Formatear la fecha del nacimiento
    const fechaFormateada = formatearFecha(nac.fechaNacimiento);

    // div para el select de los nacimientos
    const bloque = document.createElement('div');
    bloque.classList.add('mb-4', 'border', 'p-3', 'rounded');
    bloque.dataset.nacimientoIndex = nacimientoIndex;

    bloque.innerHTML = `
        <div class="d-flex justify-content-between align-items-start mb-2">

            <select id="nacimiento-${nacimientoIndex}" class="form-select" required disabled 
                onchange="guardarNacimiento(this.value);
                mostrarEjemplaresExistentes(this.value, ${nacimientoIndex})">

                <option value="${nac.id}" selected
                    data-img-macho="${nac.monta.macho.secureUrl}"
                    data-img-hembra="${nac.monta.hembra.secureUrl}">

                    ${nac.monta.macho.nombre} - ${nac.monta.hembra.nombre} (${fechaFormateada})
                </option>
            </select>

            <button type="button" class="btn btn-danger btn-sm mx-2" 
                onclick="eliminarNacimiento(this); 
                agregarNacimientoEliminado(this)">
                
                <i class="bi bi-x-lg"></i>
            </button>

        </div>

        <div id="ejemplares-${nacimientoIndex}">
            <!-- Aqui van los ejemplares dinámicamente -->
        </div>
    `;

    contenedor.appendChild(bloque);

    // Inicializar Select2 en el nuevo select creado
    $(`#nacimiento-${nacimientoIndex}`).select2({
        templateResult: formatOption,
        templateSelection: formatOption,
        minimumResultsForSearch: -1
    });

    // Mostrar ejemplares y agregar a seleccionados
    mostrarEjemplaresExistentes(nacimientoId, nacimientoIndex);

    // Agregar id nacimiento a la lista para evitar duplicados
    if (!nacimientosSeleccionados.includes(nacimientoId)) {
        nacimientosSeleccionados.push(nacimientoId);
    }
}

function mostrarEjemplaresExistentes(nacimientoId, nacimientoIndex) {
    // ¿Existe nacimiento?
    const nacimiento = listaNacimientos.find(nac => nac.id == nacimientoId);
    if(!nacimiento) return;

    // Contenedor de ejemplares
    const contenedor = document.getElementById(`ejemplares-${nacimientoIndex}`);
    contenedor.innerHTML = '';

    // Espacio entre columnas
    const row = document.createElement('div');
    row.classList.add('row', 'g-3');

    // Ejemplares del nacimiento
    nacimiento.ejemplares.forEach((ejemplar, indexEj) => {

        // ¿Ejemplar vendido?
        const ejemplarVenta = ejemplaresVenta.find(ejeV => ejeV.ejemplar.id == ejemplar.id);

        // celular, tablet chica / grande y desktop
        const col = document.createElement('div');
        col.classList.add("col-12", "col-sm-6", "col-md-4", "col-lg-3");

        // Imagenes / defaults
        const imagenes = (ejemplar.fotos && ejemplar.fotos.length > 0) ? ejemplar.fotos : [{ secureUrl: '/img/placeholder.png'}];
        const carouselId = `carouselEj${nacimientoIndex}_${indexEj}`;

        // Carrusel de imagenes
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

        let html = `
            <div class="card h-100 text-center ${ejemplarVenta ? "selected" : ""}">
                <div id="${carouselId}" class="carousel slide" data-bs-ride="carousel">
                    <div class="carousel-inner">
                        ${carouselInner}
                    </div>
                
                    ${imagenes.length > 1 ? 
                        `<button class="carousel-control-prev" type="button" data-bs-target="#${carouselId}" data-bs-slide="prev">
                            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                            <span class="visually-hidden">Previous</span>
                        </button>
                        <button class="carousel-control-next" type="button" data-bs-target="#${carouselId}" data-bs-slide="next">
                            <span class="carousel-control-next-icon" aria-hidden="true"></span>
                            <span class="visually-hidden">Next</span>
                        </button>` 
                        : 
                        ''}
                </div>

                <div class="card-body">
                    <h6 class="card-title">${ejemplar.sexo}</h6>
                    <span class="badge bg-success me-1">Precio: $${ejemplar.precio} MXM</span>
                    ${ejemplar.precioOferta ? `<span class="badge bg-danger">Oferta: $${ejemplar.precioOferta} MXM</span>` : ''}
                </div>

                <input type="hidden" name="ejemplaresVenta[${contEjemplar}].ejemplar.id" value="${ejemplar.id}">
                <input type="hidden" name="ejemplaresVenta[${contEjemplar}].ejemplar.precio" value="${ejemplar.precio}" class="precio-input">
                <input type="hidden" name="ejemplaresVenta[${contEjemplar}].ejemplar.precioOferta" value="${ejemplar.precioOferta}" class="oferta-input">
                <input type="hidden" name="ejemplaresVenta[${contEjemplar}].ejemplar.vendido" value="${ejemplarVenta ? "true" : "false"}" class="vendido-input">

                <input type="hidden" name="ejemplaresVenta[${contEjemplar}].precio" value="${ejemplar.precioOferta ? ejemplar.precioOferta : ejemplar.precio}">
        `;

        if(ejemplarVenta){
            html += `<input type="hidden" name="ejemplaresVenta[${contEjemplar}].id" value="${ejemplarVenta.id}">`;
        }

        html += `</div>`;
        // //////////////////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////////////////

        // // Primera foto de cada ejemplar
        // const primeraFoto = (ejemplar.fotos && ejemplar.fotos.length > 0) ?
        //     ejemplar.fotos[0].secureUrl : 'default.jpg';

        // // Mostrar tipo de precio (normal u oferta)
        // let tipoPrecio = (ejemplar.precio != null && ejemplar.precioOferta == null) ?
        //     "Precio" : "Oferta";    

        // // Obtener precio a mostrar
        // let precio = (ejemplar.precio != null && ejemplar.precioOferta == null) ?
        //         ejemplar.precio : ejemplar.precioOferta;

        // // ¿El ejemplar esta vendido?
        // // const ejemplarVenta = ejemplaresVenta.find(ejeV => ejeV.ejemplar.id == ejemplar.id);

        // // Mostrar ejemplar vendido
        // if(ejemplarVenta){
        //     col.innerHTML = `
        //         <div class="p-2 border rounded">
        //             <input type="hidden" name="ejemplaresVenta[${contEjemplar}].id" value="${ejemplarVenta.id}">
        //             <input type="hidden" name="ejemplaresVenta[${contEjemplar}].ejemplar.id" value="${ejemplarVenta.ejemplar.id}">

        //             <input class="form-check-input mb-2" 
        //                 type="checkbox" 
        //                 name="ejemplaresVenta[${contEjemplar}].ejemplar.vendido" 
        //                 ${ejemplarVenta.ejemplar.vendido ? 'checked' : ''}
        //                 onchange="onCheckboxChange(this)">

        //             <a href="${primeraFoto}"  target="_blank">
        //                 <img src="${primeraFoto}"  
        //                     class="img-thumbnail vista-previa" 
        //                     style="max-width: 100px; max-height: 100px; object-fit: cover;"/>
        //             </a>

        //             <div>
        //                 <p class="mb-1"><strong>Sexo:</strong> ${ejemplarVenta.ejemplar.sexo}</p>
        //                 <p class="mb-1"><strong>${tipoPrecio}: </strong>$${ejemplarVenta.precio ?? ''}</p>

        //                 <input type="hidden"
        //                     class="precio-input"
        //                     name="ejemplaresVenta[${contEjemplar}].precio"
        //                     value="${ejemplarVenta.precio ?? ''}">
        //             </div>
        //         </div>
        //     `;

        // // Mostrar ejemplar disponible
        // }else{
        //     col.innerHTML = `
        //         <div class="p-2 border rounded">
        //             <input type="hidden" name="ejemplaresVenta[${contEjemplar}].ejemplar.id" value="${ejemplar.id}">

        //             <input class="form-check-input mb-2" 
        //                 type="checkbox" 
        //                 name="ejemplaresVenta[${contEjemplar}].ejemplar.vendido" 
        //                 value="true"
        //                 onchange="onCheckboxChange(this)"
        //                 ${ejemplar.vendido ? 'checked' : ''}>

        //             <a href="${primeraFoto}"  target="_blank">
        //                 <img src="${primeraFoto}"  
        //                     class="img-thumbnail vista-previa" 
        //                     style="max-width: 100px; max-height: 100px; object-fit: cover;"/>
        //             </a>

        //             <div>
        //                 <p class="mb-1"><strong>Sexo:</strong> ${ejemplar.sexo}</p>
        //                 <p class="mb-1"><strong>${tipoPrecio}: </strong>$${precio}</p>

        //                 <input type="hidden"
        //                     class="precio-input"
        //                     name="ejemplaresVenta[${contEjemplar}].precio"
        //                     value="${precio}">
        //             </div>
        //         </div>
        //     `;
        // }

        col.innerHTML = html;

        const card = col.querySelector(".card");
        card.addEventListener("click", e => {
            // si el click fue dentro del carousel -> ignorar
            if (e.target.closest(".carousel"))
                return;

            // si el click fue fuera del carousel -> selected/unselected
            toggleSeleccion(card)
        });

        row.appendChild(col);
        contEjemplar++;
    });

    contenedor.appendChild(row);
    actualizarTotalVenta();
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//VISTA PREVIA (IMG)
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function mostrarVistaPrevia(input) {
    const file = input.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            // Busca la celda siguiente y encuentra la imagen
            const img = input.closest('tr').querySelector('.vista-previa');
            img.src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//AGREGAR LOS IDs ELIMINADOS COMO INPUTS OCULTOS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

document.getElementById("formVenta").addEventListener("submit", function () {

    articulosEliminados.forEach(id => {
        const input = document.createElement("input");
        input.type = "hidden";
        input.name = "articulosEliminados";
        input.value = id;
        this.appendChild(input);
    });

    nacimientosEliminados.forEach(id => {
        const input = document.createElement("input");
        input.type = "hidden";
        input.name = "nacimientosEliminados";
        input.value = id;
        this.appendChild(input);
    });
});

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//ACTUALIZAR SUBTOTAL, VENTA TOTAL Y RESTANTE
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function actualizarSubtotal(element) {
    const row = element.closest('tr');
    const cantidad = parseInt(row.querySelector('input[type=number]').value) || 0;
    const precio = parseFloat(row.querySelector('select').selectedOptions[0]?.dataset.precio || 0);
    row.querySelector('input[readonly]').value = (cantidad * precio).toFixed(2);

    actualizarTotalVenta();
}

function actualizarTotalVenta() {
    let total = 0;

    // Subtotales de artículos
    document.querySelectorAll('#articulosContainer input[readonly]').forEach(input => {
        total += parseFloat(input.value) || 0;
    });

    // Sumar precios solo de ejemplares seleccionados
    const contenedorEjemplares = document.getElementById('nacimientosContainer');
    if (contenedorEjemplares) {
        // contenedorEjemplares.querySelectorAll('div.col-md-6').forEach(col => { // origial
        contenedorEjemplares.querySelectorAll('.card').forEach(card => {
            const vendido = card.querySelector('.vendido-input');
            const precio = card.querySelector('.precio-input');
            const oferta = card.querySelector('.oferta-input');
            
            console.log(vendido.value);
            console.log(precio.value);
            console.log(oferta.value, typeof oferta.value);

            if(vendido?.value == "true"){
                const costo = oferta.value !== "null" ? oferta.value : precio.value;
                total += parseFloat(costo) || 0;
            }
            // const checkbox = col.querySelector('input[type="checkbox"]'); // original
            // const precioInput = col.querySelector('input.precio-input'); // original
            // if (checkbox && checkbox.checked && precioInput) { // original
                // total += parseFloat(precioInput.value) || 0;  // original
            // }
        });
    }
    
    total != 0 ? document.getElementById('totalVenta').value = total.toFixed(2) : document.getElementById('totalVenta').value = '';
    // total != 0 ? document.querySelector('input[name="totalVenta"]').value = total.toFixed(2) :
    // document.querySelector('input[name="totalVenta"]').value = '';

    // Calcular restante
    calcularRestante();
}

// Calcular restante
function calcularRestante() {
    const total = parseFloat(document.getElementById('totalVenta').value) || 0;
    const adelanto = parseFloat(document.getElementById('adelanto').value) || 0;
    const restante = total - adelanto;

    restante != 0 ? document.getElementById('restante').value = restante.toFixed(2) : document.getElementById('restante').value = '';
    // document.getElementById('restante').value = restante.toFixed(2);
}

// Calcular restante al abrir el formulario o input del adelanto
window.addEventListener('DOMContentLoaded', calcularRestante);
document.getElementById('adelanto').addEventListener('input', calcularRestante);
document.getElementById('totalVenta').addEventListener('input', calcularRestante); // sin uso porque input = readonly
