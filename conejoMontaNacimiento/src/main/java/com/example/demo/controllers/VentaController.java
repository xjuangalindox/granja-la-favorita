package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.example.demo.clients.ArticuloClient;
import com.example.demo.controllers.dto.ArticuloVentaDTO;
import com.example.demo.controllers.dto.EjemplarDTO;
import com.example.demo.controllers.dto.EjemplarVentaDTO;
import com.example.demo.controllers.dto.NacimientoDTO;
import com.example.demo.controllers.dto.VentaDTO;
import com.example.demo.models.ArticuloVentaModel;
import com.example.demo.models.EjemplarModel;
import com.example.demo.models.EjemplarVentaModel;
import com.example.demo.models.NacimientoModel;
import com.example.demo.models.VentaModel;
import com.example.demo.models.enums.EstatusVenta;
import com.example.demo.repositories.ArticuloVentaRepository;
import com.example.demo.repositories.EjemplarRepository;
import com.example.demo.repositories.EjemplarVentaRepository;
import com.example.demo.repositories.VentaRepository;
import com.example.demo.services.IArticuloVentaService;
import com.example.demo.services.IEjemplarService;
import com.example.demo.services.IEjemplarVentaService;
import com.example.demo.services.INacimientoService;
import com.example.demo.services.IVentaService;
import com.example.demo.util.ArchivoUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private ArchivoUtil archivoUtil;

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(VentaController.class);
    
    @Autowired
    private ArticuloClient articuloClient;

    @Autowired
    private IVentaService ventaService;
    
    @Autowired
    private IEjemplarService ejemplarService;

    @Autowired
    private INacimientoService nacimientoService;

    @Autowired
    private IArticuloVentaService articuloVentaService;

    @Autowired
    private IEjemplarVentaService ejemplarVentaService;


    @Autowired
    private EjemplarRepository ejemplarRepository;

    @Autowired
    private ModelMapper modelMapper;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// SELECT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Autowired
    private VentaRepository ventaRepository;

    @GetMapping("/actualizarEstatus/{id}")
    public String updateStatus(@PathVariable Long id, 
                                @RequestParam(required = false) String estatusFiltro, 
                                @RequestParam(required = false) String estatusNuevo, 
                                HttpServletRequest request) {

        VentaModel ventaModel = ventaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        // Actualizar el estatus de la venta
        if(estatusNuevo == null || estatusNuevo.isBlank()) {
            ventaModel.setEstatus(null);
        
        }else{
            try {
                ventaModel.setEstatus(EstatusVenta.valueOf(estatusNuevo.toUpperCase()));

            } catch (Exception e) {
                throw new RuntimeException("Estatus invalido: " + estatusNuevo);
            }
        }

        // Guardar los cambios
        ventaRepository.save(ventaModel);

        // Si hay filtro, regresar a esa vista filtrada
        if (estatusFiltro != null && !estatusFiltro.isBlank()) {
            return "redirect:" + archivoUtil.getBaseUrlNginx(request) + "/ventas?estatusFiltro=" + estatusFiltro;
        }

        return "redirect:" + archivoUtil.getBaseUrlNginx(request) + "/ventas";
    }
    
    @GetMapping
    public String obtenerVentas(@RequestParam(defaultValue = "0") int pagina, 
                                @RequestParam(required = false) String estatusFiltro, 
                                Model model) {

        int cantidad = 10; // Cantidad de ventas por pagina
        Page<VentaDTO> pageVentas;  // Pagina para almacenar las ventas

        // Obtener todas las ventas
        if(estatusFiltro == null || estatusFiltro.isEmpty()){
            pageVentas = ventaService.findAll(pagina, cantidad);

        // Obtener ventas sin estatus
        }else if("SIN_ESTADO".equals(estatusFiltro)){
            pageVentas = ventaService.findByEstatusIsNull(pagina, cantidad);

        // Obtener ventas por estatus especifico
        }else{
            pageVentas = ventaService.findByEstatus(pagina, cantidad, EstatusVenta.valueOf(estatusFiltro.toUpperCase()));
        }

        // pageVentas = estatus != null && !estatus.isEmpty() ?
        //     ventaService.findByEstatus(pagina, cantidad, EstatusVenta.valueOf(estatus.toUpperCase())) : ventaService.findAll(pagina, cantidad);

        model.addAttribute("listaEstatus", EstatusVenta.values()); // Enviar lista de estatus
        model.addAttribute("estatusFiltro", estatusFiltro); // Estatus seleccionado
        model.addAttribute("listaVentas", pageVentas.getContent()); // Ventas de la pagina actual
        model.addAttribute("paginaActual", pageVentas.getNumber()); // Página actual
        model.addAttribute("totalPaginas", pageVentas.getTotalPages()); // Total de páginas
        model.addAttribute("totalElementos", pageVentas.getTotalElements()); // Total de elementos

        return "ventas/lista";
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// INSERT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/crear")
    public String formularioCrear(Model model) {
        model.addAttribute("ventaDTO", new VentaDTO());
        model.addAttribute("titulo", "Registrar Venta");
        model.addAttribute("accion", "/ventas/guardar");
        model.addAttribute("listaArticulos", articuloClient.obtenerArticulos());
        model.addAttribute("listaNacimientos", getNacimientosConEjemplaresDisponibles());
        // model.addAttribute("listaNacimientos", filtrarNacimientosConEjemplaresDisponibles(nacimientoService.obtenerNacimientos()));

        return "ventas/formulario";
    }

    @PostMapping("/guardar")
    public String guardarVenta(@ModelAttribute("ventaDTO") VentaDTO ventaDTO, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        List<ArticuloVentaDTO> articulosVenta = new ArrayList<>();
        List<EjemplarVentaDTO> ejemplaresVenta = new ArrayList<>();

        // 1. Filtrar articulos y ejemplares validos
        if(ventaDTO.getArticulosVenta() != null && !ventaDTO.getArticulosVenta().isEmpty()){
            articulosVenta = filtrarArticulosNuevos(ventaDTO.getArticulosVenta());
        }
        if(ventaDTO.getEjemplaresVenta() != null && !ventaDTO.getEjemplaresVenta().isEmpty()){
            ejemplaresVenta = filtrarEjemplaresNuevos(ventaDTO.getEjemplaresVenta());
        }

        // 2. Persistir por separado: venta, articulosVenta y ejemplaresVenta (nuevos)
        VentaDTO venta;

        try {
            venta = ventaService.guardarVenta(ventaDTO);

            if(articulosVenta != null && !articulosVenta.isEmpty()){
                articulosVenta.forEach(articuloVenta -> {
                    articuloVenta.setVenta(venta);
                    articuloVentaService.guardarArticuloVenta(articuloVenta);
                });
            }

            if(ejemplaresVenta != null && !ejemplaresVenta.isEmpty()){
                ejemplaresVenta.forEach(ejemplarVenta -> {
                    ejemplarVenta.setVenta(venta);
                    ejemplarVentaService.guardarEjemplarVenta(ejemplarVenta);
                });
            }

        } catch (Exception e) {
            model.addAttribute("ventaDTO", ventaDTO);
            model.addAttribute("titulo", "Registrar Venta");
            model.addAttribute("accion", "/ventas/guardar");
            model.addAttribute("listaArticulos", articuloClient.obtenerArticulos());
            model.addAttribute("listaNacimientos", getNacimientosConEjemplaresDisponibles());
            // model.addAttribute("listaNacimientos", filtrarNacimientosConEjemplaresDisponibles(nacimientoService.obtenerNacimientos()));

            model.addAttribute("mensaje", "Ocurrió un error al registrar la venta.");
            return "ventas/formulario";
        }

        // 3. Mostrar venta registrada
        redirectAttributes.addFlashAttribute("ok", "Venta registrada correctamente.");
        // return "redirect:/ventas";
        // return "redirect:"+archivoUtil.getBaseUrl(request)+"/ventas";
        return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/ventas";
    } 
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// UPDATE
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // @GetMapping("venta/{id}")
    // public ResponseEntity<VentaDTO> infoVenta(@PathVariable("id") Long id){
    //     VentaDTO ventaDTO = ventaService.obtenerVentaPorId(id)
    //         .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

    //     return ResponseEntity.ok(ventaDTO);
    // }

    // @GetMapping("utilizados/{id}")
    // public ResponseEntity<Set<Long>> prueba(@PathVariable("id") Long id){
    //     VentaDTO ventaDTO = ventaService.obtenerVentaPorId(id)
    //         .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

    //     Set<Long> idsUnicos = this.getIdsNacimientosUtilizados(ventaDTO.getEjemplaresVenta());

    //     return ResponseEntity.ok(idsUnicos);
    // }

    // @GetMapping("/ejemplares/{id}")
    // public ResponseEntity<List<NacimientoDTO>> nacimientosEjemplaresDisponibles(@PathVariable("id") Long id) {
    //     VentaDTO ventaDTO = ventaService.obtenerVentaPorId(id)
    //         .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

    //     List<NacimientoDTO> disponibles = this.filtrarNacimientosConEjemplaresDisponibles(nacimientoService.obtenerNacimientos());
    //     disponibles = this.fusionarEjemplaresVendidosConEjemplaresDisponibles(ventaDTO.getEjemplaresVenta(), disponibles);

    //     disponibles.forEach(nac -> {
    //         /*nac.setFechaNacimiento(null);
    //         nac.setGazaposVivos(null);
    //         nac.setGazaposMuertos(null);
    //         nac.setNota(null);*/
    //         nac.setMonta(null);
    //     });

    //     return ResponseEntity.ok(disponibles);
    // }
    

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        
        Optional<VentaDTO> ventaOpt = ventaService.obtenerVentaPorId(id);
        if(ventaOpt.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "Venta no encontrada.");
            // return "redirect:/ventas";
            // return "redirect:"+archivoUtil.getBaseUrl(request)+"/ventas";
            return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/ventas";
        }

        VentaDTO ventaDTO = ventaOpt.get();

        // List<NacimientoDTO> listaNacimientos = filtrarNacimientosConEjemplaresDisponibles(nacimientoService.obtenerNacimientos());
        List<NacimientoDTO> listaNacimientos = getNacimientosConEjemplaresDisponibles();
        listaNacimientos = fusionarEjemplaresVendidosConEjemplaresDisponibles(ventaDTO.getEjemplaresVenta(), listaNacimientos);

        model.addAttribute("ventaDTO", ventaDTO);
        model.addAttribute("titulo", "Editar Venta");
        model.addAttribute("accion", "/ventas/editar/"+id);
        
        model.addAttribute("listaArticulos", articuloClient.obtenerArticulos());
        model.addAttribute("listaNacimientos", listaNacimientos); // nacimientos con ejemplares vendidos y disponibles, vendidos al final de cada nac
        model.addAttribute("idsNacimientosUtilizados", getIdsNacimientosUtilizados(ventaDTO.getEjemplaresVenta()));

        return "ventas/formulario";
    }

    @PostMapping("/editar/{id}")
    public String editarVenta(@PathVariable("id") Long id, @ModelAttribute("ventaDTO") VentaDTO ventaDTO, HttpServletRequest request,
        @RequestParam(name = "articulosEliminados", required = false) List<Long> idsArticulosVentaEliminados,
        @RequestParam(name = "nacimientosEliminados", required = false) List<Long> idsNacimientosEliminados,
        Model model, RedirectAttributes redirectAttributes) {

        try {
            ventaService.editarVenta(id, ventaDTO, idsArticulosVentaEliminados, idsNacimientosEliminados);
            redirectAttributes.addFlashAttribute("ok", "Venta modificada correctamente");
            // return "redirect:/ventas";
            // return "redirect:"+archivoUtil.getBaseUrl(request)+"/ventas";
            return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/ventas";

        } catch (Exception e) {
            model.addAttribute("ventaDTO", ventaDTO);
            model.addAttribute("titulo", "Editar Venta");
            model.addAttribute("accion", "/ventas/editar/"+id);
            
            // List<NacimientoDTO> listaNacimientos = filtrarNacimientosConEjemplaresDisponibles(nacimientoService.obtenerNacimientos());
            List<NacimientoDTO> listaNacimientos = getNacimientosConEjemplaresDisponibles();
            listaNacimientos = fusionarEjemplaresVendidosConEjemplaresDisponibles(ventaDTO.getEjemplaresVenta(), listaNacimientos);

            model.addAttribute("listaArticulos", articuloClient.obtenerArticulos());
            model.addAttribute("listaNacimientos", listaNacimientos);
            model.addAttribute("idsNacimientosUtilizados", getIdsNacimientosUtilizados(ventaDTO.getEjemplaresVenta()));

            model.addAttribute("error", "Ocurrio un error al modificar la venta");
            return "ventas/formulario";
        }

        /*System.out.println("\n\n");
        System.out.println("idsArticulosVentaEliminados: "+idsArticulosVentaEliminados);
        System.out.println("idsNacimientosEliminados: "+idsNacimientosEliminados);
        System.out.println("\n\n");

        System.out.println("\n\n");
        System.out.println("articulos nuevos: "+articulosNuevos);
        System.out.println("\n");
        System.out.println("articulos modificados: "+articulosExistentes);
        System.out.println("\n");
        System.out.println("ejemplares nuevos: "+ejemplaresNuevos);
        System.out.println("\n");
        System.out.println("ejemplares modificados: "+ejemplaresExistentes);
        System.out.println("\n\n");

        // 3. Persistir por separado: venta, articulosVenta y ejemplaresVenta (nuevos y existentes)
        VentaDTO venta;*/
        
        /*try {
            venta = ventaService.editarVenta(id, ventaDTO);

            if(articulosNuevos != null && !articulosNuevos.isEmpty()){
                articulosNuevos.forEach(articuloVenta -> {
                    articuloVenta.setVenta(venta);
                    articuloVentaService.guardarArticuloVenta(articuloVenta);
                });
            }
            if(ejemplaresNuevos != null && !ejemplaresNuevos.isEmpty()){
                ejemplaresNuevos.forEach(ejemplarVenta -> {
                    ejemplarVenta.setVenta(venta);
                    ejemplarVentaService.guardarEjemplarVenta(ejemplarVenta);
                });
            }

            if(articulosExistentes != null && !articulosExistentes.isEmpty()){
                articulosExistentes.forEach(articuloVenta -> articuloVentaService.editarArticuloVenta(articuloVenta.getId(), articuloVenta));
            }
            if(ejemplaresExistentes != null && !ejemplaresExistentes.isEmpty()){
                for(EjemplarVentaDTO ejemplarVentaDTO : ejemplaresExistentes){
                    // Ejemplar sigue vendido
                    if(ejemplarVentaDTO.getEjemplar().isVendido()){
                        ejemplarVentaService.editarEjemplarVenta(ejemplarVentaDTO.getId(), ejemplarVentaDTO);

                    // Ejemplar paso a disponible
                    }else{
                        try {
                            // 1. Liberar ejemplar
                            // Obtener ejemplar
                            Optional<EjemplarDTO> ejemplarOpt = ejemplarService.obtenerEjemplarPorId(ejemplarVentaDTO.getEjemplar().getId());
                            EjemplarDTO ejemplarDTO = ejemplarOpt.get();

                            // Liberar ejemplar
                            ejemplarDTO.setVendido(false);
                            ejemplarService.editarEjemplar(ejemplarDTO);

                            // 2. Eliminar ejemplar venta
                            // Obtener ejemplar venta
                            Optional<EjemplarVentaModel> ejemplarVentaModelOpt = ejemplarVentaRepository.findById(ejemplarVentaDTO.getId());
                            EjemplarVentaModel ejemplarVentaModel = ejemplarVentaModelOpt.get();

                            // Desvincular ejemplar venta de venta
                            ejemplarVentaModel.setVenta(null);
                            ejemplarVentaRepository.save(ejemplarVentaModel);

                            // Eliminar ejemplar venta
                            ejemplarVentaRepository.deleteById(ejemplarVentaModel.getId());

                        } catch (Exception e) {
                            // Revisar porque se ejecuta esta exception
                            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al liberar el ejemplar o eliminar el ejemplar venta");
                            return "redirect:/ventas";
                        }
                    }
                }
            }

        } catch (Exception e) {
            model.addAttribute("ventaDTO", ventaDTO);
            model.addAttribute("titulo", "Editar Venta");
            model.addAttribute("accion", "/ventas/editar/"+id);
            
            model.addAttribute("listaArticulos", articuloService.obtenerArticulos());
            model.addAttribute("listaNacimientos", filtrarNacimientosConEjemplaresDisponibles(nacimientoService.obtenerNacimientos()));
            model.addAttribute("idsNacimientosUtilizados", getIdsNacimientosUtilizados(ventaDTO.getEjemplaresVenta()));

            model.addAttribute("mensaje", "Ocurrio un error al editar la venta.");
            return "ventas/formulario";
        }

        redirectAttributes.addFlashAttribute("ok", "Venta modificada correctamente.");
        return "redirect:/ventas";*/
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DELETE - READY
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    @GetMapping("/eliminar/{id}")
    public String eliminarVenta(@PathVariable("id") Long id, 
                                @RequestParam(required = false) String estatusFiltro,
                                RedirectAttributes redirectAttributes, 
                                HttpServletRequest request) {
        try {
            ventaService.eliminarVenta(id);
            redirectAttributes.addFlashAttribute("ok", "Venta eliminada correctamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // Si hay filtro, regresar a esa vista filtrada
        if (estatusFiltro != null && !estatusFiltro.isBlank()) {
            return "redirect:" + archivoUtil.getBaseUrlNginx(request) + "/ventas?estatusFiltro=" + estatusFiltro;
        }

        // return "redirect:/ventas";
        // return "redirect:"+archivoUtil.getBaseUrl(request)+"/ventas";
        return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/ventas";

        // Optional<VentaDTO> ventaOpt = ventaService.obtenerVentaPorId(id);
        // if(ventaOpt.isEmpty()){
        //     redirectAttributes.addFlashAttribute("error", "Venta no encontrada.");
        //     // return "redirect:/ventas";
        //     return "redirect:"+archivoUtil.getBaseUrl(request)+"/ventas";
        // }

        // try {
        //     ventaService.eliminarVenta(ventaOpt.get());
        //     redirectAttributes.addFlashAttribute("ok", "Venta eliminada correctamente.");

        // } catch (Exception e) {
        //     redirectAttributes.addFlashAttribute("error", "Ocurrió un error al eliminar la venta.");
        // }
        
        // // return "redirect:/ventas";
        // return "redirect:"+archivoUtil.getBaseUrl(request)+"/ventas";
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ENDPOINT PRUEBA
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // @GetMapping("/prueba/{id}")
    // public ResponseEntity<VentaDTO> metodoPrueba(@PathVariable("id") Long id){
    //     Optional<VentaDTO> ventaOpt = ventaService.obtenerVentaPorId(id);
    //     if(ventaOpt.isPresent()){
    //         return ResponseEntity.ok(ventaOpt.get());
    //     }else{
    //         return ResponseEntity.notFound().build();
    //     }
    // }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FILTROS (NUEVOS Y EXISTENTES) Y EJEMPLARES DISPONIBLES (EN NACIMIENTO)
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<ArticuloVentaDTO> filtrarArticulosNuevos(List<ArticuloVentaDTO> articulosVenta){
        return articulosVenta.stream()
            .filter(item -> 
                item.getId() == null &&
                item.getCantidad() != null &&
                item.getSubtotal() != null &&
                item.getArticulo() != null &&
                item.getArticulo().getId() != null)
            .collect(Collectors.toList());
    }

    private List<ArticuloVentaDTO> filtrarArticulosExistentes(List<ArticuloVentaDTO> articulosVenta) {
        return articulosVenta.stream()
            .filter(item -> item.getId() != null &&
                item.getCantidad() != null &&
                item.getSubtotal() != null &&
                item.getArticulo() != null &&
                item.getArticulo().getId() != null)
            .collect(Collectors.toList());
    }

    private List<EjemplarVentaDTO> filtrarEjemplaresNuevos(List<EjemplarVentaDTO> ejemplaresVenta) {
        return ejemplaresVenta.stream()
            .filter(ejeVenta -> 
                ejeVenta.getId() == null &&
                ejeVenta.getPrecio() != null &&
                ejeVenta.getEjemplar() != null &&
                ejeVenta.getEjemplar().getId() != null &&
                ejeVenta.getEjemplar().isVendido())
            .collect(Collectors.toList());
    }

    private List<EjemplarVentaDTO> filtrarEjemplaresExistentes(List<EjemplarVentaDTO> ejemplaresVenta) {
        return ejemplaresVenta.stream()
            .filter(item -> 
                item.getId() != null &&
                //item.getPrecio() != null &&
                item.getEjemplar() != null &&
                item.getEjemplar().getId() != null)
            .collect(Collectors.toList());
    }

    // Filtrar lista de nacimientos con ejemplares disponibles, 
    private List<NacimientoDTO> filtrarNacimientosConEjemplaresDisponibles(List<NacimientoDTO> listaNacimientos){
        if(listaNacimientos == null || listaNacimientos.isEmpty()){
            return listaNacimientos;
        }

        return listaNacimientos.stream()
            .map(nac -> {
                List<EjemplarDTO> disponibles = nac.getEjemplares().stream()
                    .filter(eje -> !eje.isVendido())
                    .collect(Collectors.toList());
                    
                nac.setEjemplares(disponibles);
                return nac;
            })
            .filter(nac -> nac.getEjemplares() != null && !nac.getEjemplares().isEmpty())
            .collect(Collectors.toList());
    }

    // Obtener idNacimiento de cada EjemplarVenta (sin duplicados)
    private Set<Long> getIdsNacimientosUtilizados(List<EjemplarVentaDTO> ejemplaresVenta){
        Set<Long> idsNacimientosUnicos = new HashSet<>();

        if(ejemplaresVenta != null && !ejemplaresVenta.isEmpty()){
            ejemplaresVenta.forEach(eje -> idsNacimientosUnicos.add(eje.getEjemplar().getNacimiento().getId()));
        }
        
        return idsNacimientosUnicos;
    }

    // Agregar ejemplar vendidos a la lista de nacimientos
    private List<NacimientoDTO> fusionarEjemplaresVendidosConEjemplaresDisponibles(List<EjemplarVentaDTO> vendidos, List<NacimientoDTO> listaNacimientos){
        vendidos.forEach(ejemplarVenta -> {
            Long idNacimiento = ejemplarVenta.getEjemplar().getNacimiento().getId();
            boolean encontrado = false;

            for(NacimientoDTO nac : listaNacimientos){
                if(nac.getId().equals(idNacimiento)){
                    // Agregar ejemplar vendido al nacimiento
                    nac.getEjemplares().add(ejemplarVenta.getEjemplar());
                    encontrado = true;
                    break;
                }
            }

            if(!encontrado){
                // Obtener nacimiento de la base de datos
                Optional<NacimientoDTO> nacimientoOpt = nacimientoService.obtenerNacimientoById(idNacimiento);
                if(nacimientoOpt.isPresent()){
                    NacimientoDTO nacimientoDTO = nacimientoOpt.get();
                    // Limpiar lista de ejemplares y agregar ejemplar vendido
                    nacimientoDTO.setEjemplares(new ArrayList<>());
                    nacimientoDTO.getEjemplares().add(ejemplarVenta.getEjemplar());
                    // Agregar nacimiento a la lista de nacimientos
                    listaNacimientos.add(nacimientoDTO);
                }
            }
        });

        return listaNacimientos;
    }

    public List<NacimientoDTO> getNacimientosConEjemplaresDisponibles(){
        
        // Obtener todos los ejemplares disponibles
        List<EjemplarModel> ejemplaresDisponibles = ejemplarRepository.findByVendido(false);
        
        // Obtener los nacimientos de los ejemplares disponibles
        Set<NacimientoModel> nacimientosUnicos = ejemplaresDisponibles.stream()
            .map(ejemplar -> ejemplar.getNacimiento())
            .collect(Collectors.toSet());

        // Convertir los nacimientos a DTO y filtrar los que tienen ejemplares disponibles
        // (es decir, ejemplares que no están vendidos)
        List<NacimientoDTO> nacimientosConEjemplaresDisponibles = nacimientosUnicos.stream()
            .map(nacimiento -> {
                nacimiento.setEjemplares(
                    nacimiento.getEjemplares().stream()
                        .filter(ejemplar -> !ejemplar.isVendido())
                        .collect(Collectors.toList())
                );
                
                return modelMapper.map(nacimiento, NacimientoDTO.class);
            })
            .filter(nacimiento -> !nacimiento.getEjemplares().isEmpty()) // Filtrar nacimientos que tienen todos los ejemplares vendidos
            .collect(Collectors.toList());

        return nacimientosConEjemplaresDisponibles;
    }
}
