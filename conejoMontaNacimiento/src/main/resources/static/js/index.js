// let next = document.querySelector('.next')
// let prev = document.querySelector('.prev')

// next.addEventListener('click', function(){
//     let items = document.querySelectorAll('.item')
//     document.querySelector('.slide').appendChild(items[0])
// })

// prev.addEventListener('click', function(){
//     let items = document.querySelectorAll('.item')
//     document.querySelector('.slide').prepend(items[items.length - 1]) // here the length of items = 6
// })

// Seleccionamos la raíz del carrusel
const carousel = document.querySelector('.carousel-container');

// Botones dentro del carrusel
const next = carousel.querySelector('.button .next');
const prev = carousel.querySelector('.button .prev');

// Contenedor de las imágenes
const slide = carousel.querySelector('.slide');

// Evento del botón "next"
next.addEventListener('click', function() {
    const items = slide.querySelectorAll('.item');
    slide.appendChild(items[0]); // Mueve el primer item al final
});

// Evento del botón "prev"
prev.addEventListener('click', function() {
    const items = slide.querySelectorAll('.item');
    slide.prepend(items[items.length - 1]); // Mueve el último item al inicio
});