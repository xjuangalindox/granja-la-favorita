window.addEventListener('DOMContentLoaded', () => {    
    if(ejemplares.length > 0){
        ejemplares.forEach(eje => agregarEjemplarExistente(eje));
    }
});

let contador = 0;

function mostrarVistaPreviaMultiple(input) {
    const contenedor = input.nextElementSibling; // div.previews
    contenedor.innerHTML = ""; // limpiar vistas previas previas

    if (input.files) {
        Array.from(input.files).forEach(file => {
            const reader = new FileReader();
            reader.onload = e => {
                const img = document.createElement("img");
                img.src = e.target.result;
                img.className = "img-thumbnail";
                img.style.width = "80px";
                img.style.height = "80px";
                img.style.objectFit = "cover";
                contenedor.appendChild(img);
            };
            reader.readAsDataURL(file);
        });
    }
}

// Crear ejemplar (responsivo y compacto)
function agregarEjemplar() {
    const tbody = document.getElementById('ejemplaresContainer');
    const row = document.createElement('tr');
    row.classList.add('text-center', 'align-middle');

        // <td>
        //     <div class="d-flex flex-column align-items-center">
        //         <input class="form-control form-control-sm campo-imagen" type="file" name="ejemplares[${contador}].imagen" accept="image/*" required 
        //             onchange="mostrarVistaPrevia(this)">
        //         <img class="img-thumbnail ejemplar-img mt-1" style="object-fit:cover;"/>
        //     </div>
        // </td>

    row.innerHTML = `

        <td>
            <div class="d-flex flex-column align-items-center">
                <input class="form-control form-control-sm campo-imagen" 
                        type="file" 
                        name="ejemplares[${contador}].imagenes" 
                        accept="image/*" 
                        multiple
                        required 
                        onchange="mostrarVistaPreviaMultiple(this)">

                <div class="previews mt-1 d-flex flex-wrap gap-1"></div>
            </div>
        </td>

        <td>
            <select class="form-select form-select-sm text-center" name="ejemplares[${contador}].sexo" required>
                <option value="">Sexo</option>
                <option value="Macho">Macho</option>
                <option value="Hembra">Hembra</option>
            </select>
        </td>
        <td>
            <div class="d-flex justify-content-center">
                <input class="form-control form-control-sm campo-precio text-center" type="number" name="ejemplares[${contador}].precio" min="0" step="1" required>
            </div>
        </td>
        <td>
            <div class="d-flex justify-content-center">
                <input class="form-control form-control-sm campo-oferta text-center" type="number" name="ejemplares[${contador}].precioOferta" min="0" step="1">
            </div>
            
        </td>
        <td>
            <input class="form-check-input" type="checkbox" name="ejemplares[${contador}].vendido" disabled>
        </td>
        <td>
            <button type="button" class="btn btn-danger btn-sm" onclick="eliminarEjemplar(this)">
                <i class="bi bi-x-lg"></i>
            </button>
        </td>
    `;
    tbody.appendChild(row);
    contador++;
}

// Ejemplar existente (responsivo y compacto)
function agregarEjemplarExistente(eje) {
    const tbody = document.getElementById('ejemplaresContainer');
    const row = document.createElement('tr');
    row.classList.add('text-center', 'align-middle');
        
        // <input type="hidden" name="ejemplares[${contador}].publicId" value="${eje.publicId}"/>
        // <input type="hidden" name="ejemplares[${contador}].secureUrl" value="${eje.secureUrl}"/>

        // <td>
        //     <div class="d-flex flex-column align-items-center">
        //         <input type="file" name="ejemplares[${contador}].imagen" class="form-control form-control-sm campo-imagen" accept="image/*" 
        //             onchange="mostrarVistaPrevia(this)">
        //         <a href="${eje.secureUrl}" target="_blank">
        //             <img class="img-thumbnail ejemplar-img mt-1" src="${eje.secureUrl}" style="object-fit:cover;"/>
        //         </a>
        //     </div>
        // </td>

    row.innerHTML = `
        <input type="hidden" name="ejemplares[${contador}].id" value="${eje.id}"/>

        <td>
            <div class="d-flex flex-column align-items-center">

                <input class="form-control form-control-sm campo-imagen" 
                        type="file" 
                        name="ejemplares[${contador}].imagenes" 
                        accept="image/*" 
                        multiple 
                        onchange="mostrarVistaPreviaMultiple(this)">

                <div class="previews mt-1 d-flex flex-wrap gap-1">
                    ${eje.fotos.map((foto, index) => `
                        <div class="preview-item position-relative me-1 mb-1">
                            <img class="img-thumbnail" src="${foto.secureUrl}" style="object-fit:cover; width:80px; height:80px;">
                            <input type="hidden" name="ejemplares[${contador}].fotos[${index}].id" value="${foto.id}">
                            <input type="hidden" name="ejemplares[${contador}].fotos[${index}].publicId" value="${foto.publicId}">
                            <input type="hidden" name="ejemplares[${contador}].fotos[${index}].secureUrl" value="${foto.secureUrl}">
                        </div>
                    `).join('')}
                </div>

            </div>
        </td>

        <td>
            <select class="form-select form-select-sm text-center" name="ejemplares[${contador}].sexo" required>
                <option value="">Sexo</option>
                <option value="Macho" ${eje.sexo === 'Macho' ? 'selected' : ''}>Macho</option>
                <option value="Hembra" ${eje.sexo === 'Hembra' ? 'selected' : ''}>Hembra</option>
            </select>
        </td>
        <td>
            <div class="d-flex justify-content-center">
                <input class="form-control form-control-sm campo-precio text-center" type="number" name="ejemplares[${contador}].precio" min="0" step="1" 
                    value="${eje.precio != null ? eje.precio : ''}" required>
            </div>
        </td>
        <td>
            <div class="d-flex justify-content-center">
                <input class="form-control form-control-sm campo-oferta text-center" type="number" name="ejemplares[${contador}].precioOferta" min="0" step="1" 
                    value="${eje.precioOferta != null ? eje.precioOferta : ''}">
            </div>
        </td>
        <td>
            <input class="form-check-input" type="checkbox" name="ejemplares[${contador}].vendido" ${eje.vendido ? 'checked' : ''} disabled>
        </td>
        <td>
            <button type="button" class="btn btn-danger btn-sm" onclick="eliminarEjemplar(this)">
                <i class="bi bi-x-lg"></i>
            </button>
        </td>
    `;
    tbody.appendChild(row);
    contador++;
}

// Vista previa de imagen
function mostrarVistaPrevia(input) {
    const file = input.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const img = input.closest('tr').querySelector('.ejemplar-img');
            img.src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
}

let ejemplaresEliminados = [];

// Eliminar ejemplar
function eliminarEjemplar(boton) {
    const row = boton.closest('tr');
    const inputId = row.querySelector('input[type="hidden"][name$=".id"]');
    if(inputId && inputId.value){
        ejemplaresEliminados.push(parseInt(inputId.value));
    }
    row.remove();
}

// Agregar IDs eliminados como inputs ocultos
document.getElementById("formNacimiento").addEventListener("submit", function () {
    ejemplaresEliminados.forEach(id => {
        const input = document.createElement("input");
        input.type = "hidden";
        input.name = "ejemplaresEliminados";
        input.value = id;
        this.appendChild(input);
    });
});

// Validaciones de precios
document.getElementById('formNacimiento').addEventListener('submit', function(event) {
    const precios = this.querySelectorAll('.campo-precio');
    const preciosOferta = this.querySelectorAll('.campo-oferta');
    for(let i = 0; i < precios.length; i++) {
        const precio = parseFloat(precios[i].value);
        const precioOferta = parseFloat(preciosOferta[i].value);
        if (!isNaN(precioOferta) && (isNaN(precio) || precio === 0)) {
            event.preventDefault();
            alert(`Debe ingresar un precio normal antes de establecer un precio de oferta en el ejemplar #${i + 1}.`);
            precios[i].focus();
            return false;
        }
        if (!isNaN(precio) && !isNaN(precioOferta)) {
            if (precioOferta >= precio) {
                event.preventDefault();
                alert(`El precio de oferta no puede ser mayor o igual al precio normal en el ejemplar #${i + 1}.`);
                preciosOferta[i].focus();
                return false;
            }
        }
    }
});