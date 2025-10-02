package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import com.example.demo.clients.RazaClient;
import com.example.demo.controllers.dto.ConejoDTO;
import com.example.demo.controllers.dto.RazaDTO;
import com.example.demo.models.ConejoModel;
import com.example.demo.repositories.ConejoRepository;
import com.example.demo.repositories.MontaRepository;
import com.example.demo.util.ArchivoUtil;

@ExtendWith(MockitoExtension.class)
public class ConejoServiceTest {

    @Mock
    private ArchivoUtil archivoUtil;

    @Mock
    private RazaClient razaClient;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ConejoRepository conejoRepository;

    @Mock
    private MontaRepository montaRepository;

    @InjectMocks
    private ConejoServiceImpl conejoService;

    MockMultipartFile imagen, imagenVacia;
    Map<String, Object> mapa;

    ConejoModel enojona, marino, mexicana, panda, rocko, semental, trueno; // asc
    // ConejoModel trueno, semental, rocko, panda, mexicana, marino, enojona; // desc

    RazaDTO minilop, leon;

    @BeforeEach
    void setup(){
        mapa = Map.of("public_id", "123abc");
        imagen = new MockMultipartFile("imagen", new byte[]{1, 2, 3});
        imagenVacia = new MockMultipartFile("imagen", new byte[0]);

        // Conejos inactivos
        semental = new ConejoModel(1L, null, null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://cloudinary.com/semental.png", null, null, null, 1L);
        panda = new ConejoModel(2L, null, null,"Panda", "Hembra", null, false, 
        "Abuelita, jubilada", "123abc", "https://cloudinary.com/panda.png", null, null, null, 1L);

        // Conejos activos
        rocko = new ConejoModel(3L, null, null,"Rocko", "Macho", null, true, 
        "Tiene malito el ojo", "123abc", "https://cloudinary.com/rocko.png", null, null, null, 1L);
        trueno = new ConejoModel(4L, null, null,"Trueno", "Macho", null, true, 
        "Tiene lloroso el ojo", "123abc", "https://cloudinary.com/trueno.png", null, null, null, 1L);
        marino = new ConejoModel(5L, null, null,"Marino", "Macho", null, true, 
        "Tiene gripa", "123abc", "https://cloudinary.com/marino.png", null, null, null, 1L);

        mexicana = new ConejoModel(6L, null, null,"Mexicana", "Hembra", null, true, 
        "Perfecto estado", "123abc", "https://cloudinary.com/mexicana.png", null, null, null, 1L);
        enojona = new ConejoModel(7L, null, null,"Enojona", "Hembra", null, true, 
        "Perfecto estado", "123abc", "https://cloudinary.com/enojona.png", null, null, null, 1L);

        // Razas
        minilop = new RazaDTO(1L, "Minilop");
        leon = new RazaDTO(2L, "Cabeza de León");
    }

    private ConejoDTO mapModeltoDto(ConejoModel conejoModel){
        ConejoDTO dto = new ConejoDTO();
        dto.setId(conejoModel.getId());
        dto.setNombre(conejoModel.getNombre());
        dto.setSexo(conejoModel.getSexo());
        dto.setPeso(conejoModel.getPeso());
        dto.setActivo(conejoModel.isActivo());
        dto.setNota(conejoModel.getNota());
        dto.setPublicId(conejoModel.getPublicId());
        dto.setSecureUrl(conejoModel.getSecureUrl());
        dto.setFechaNacimiento(conejoModel.getFechaNacimiento());
        dto.setTotalNacimientos(conejoModel.getTotalNacimientos());
        dto.setTotalGazapos(conejoModel.getTotalGazapos());

        return dto;
    }

    @Test
    void testFindAll(){
        // given
        List<ConejoModel> lista = java.util.Arrays.asList(semental, panda, rocko, trueno, marino, mexicana, enojona);
        Page<ConejoModel> pageConejos = new PageImpl<>(lista);

        // when: repository
        when(conejoRepository.findAll(any(Pageable.class))).thenReturn(pageConejos);
        
        // when: razaClient
        when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop);

        // when: modelMapper
        when(modelMapper.map(any(ConejoModel.class), eq(ConejoDTO.class))).thenAnswer(invocation -> mapModeltoDto(invocation.getArgument(0)));
        // when(modelMapper.map(any(ConejoModel.class), eq(ConejoDTO.class))).thenAnswer(invocation -> {
        //     ConejoModel conejoModel = invocation.getArgument(0);
        //     ConejoDTO conejoDTO = mapModeltoDto(conejoModel);
        //     return conejoDTO;
        // });

        // then
        Page<ConejoDTO> pagina = conejoService.findAll(0, 10);
        assertNotNull(pagina);
        assertEquals(7, pagina.getSize()); // Tamaño de la pagina actual porque hay 7 conejos y 3 vacios
        assertEquals(1, pagina.getTotalPages()); // solo una pigina porque hay 7 conejos en total
        assertEquals(7, pagina.getNumberOfElements()); // Tamaño de la pagina actual porque hay 7 conejos y 3 vacios
        assertEquals(7, pagina.getTotalElements()); // Total de conejos en la bd

        List<ConejoDTO> listaConejos = pagina.getContent();
        assertEquals("Semental", listaConejos.get(0).getNombre());
        assertEquals("Panda", listaConejos.get(1).getNombre());
        assertEquals("Rocko", listaConejos.get(2).getNombre());
        assertEquals("Trueno", listaConejos.get(3).getNombre());
        assertEquals("Marino", listaConejos.get(4).getNombre());
        assertEquals("Mexicana", listaConejos.get(5).getNombre());
        assertEquals("Enojona", listaConejos.get(6).getNombre());

        verify(conejoRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testFindBySexo(){
        // given
        List<ConejoModel> lista = Arrays.asList(panda, mexicana, enojona);
        Page<ConejoModel> page = new PageImpl<>(lista);

        // when
        when(conejoRepository.findBySexo(any(Pageable.class), anyString())).thenReturn(page);
        when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop);
        when(modelMapper.map(any(ConejoModel.class), eq(ConejoDTO.class))).thenAnswer(invocation -> mapModeltoDto(invocation.getArgument(0)));

        // then
        Page<ConejoDTO> pageConejos = conejoService.findBySexo(0, 5, "Hembra");

        assertNotNull(pageConejos);
        assertEquals(3, pageConejos.getNumberOfElements()); // la pagina contiene 3 hembras y 2 vacios
        assertEquals(1, pageConejos.getTotalPages()); // cada pagina tiene 5 conejos pero solo hay 3 hembras en la bd
        assertEquals(3, pageConejos.getTotalElements()); // hay 3 hembras en total en la bd

        List<ConejoDTO> listaConejos = pageConejos.getContent();
        assertNotNull(listaConejos);
        assertEquals(3, listaConejos.size());
        assertEquals("Panda", listaConejos.get(0).getNombre());
        assertEquals("Mexicana", listaConejos.get(1).getNombre());
        assertEquals("Enojona", listaConejos.get(2).getNombre());

        verify(conejoRepository, times(1)).findBySexo(any(Pageable.class), anyString());
    }

    @Test
    void testObtenerConejoById(){
        // when
        when(conejoRepository.findById(anyLong())).thenReturn(Optional.of(marino)); // Retornar Model
        when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop); // Retornar DTO
        when(modelMapper.map(any(ConejoModel.class), eq(ConejoDTO.class))).thenReturn(mapModeltoDto(marino));

        // then
        Optional<ConejoDTO> opt = conejoService.obtenerConejoById(5L);
        assertNotNull(opt);
        assertTrue(opt.isPresent());
        assertEquals("Marino", opt.get().getNombre());

        verify(conejoRepository, times(1)).findById(anyLong());
    }

    @Test
    void testObtenerConejos(){
        // asc: enojona, marino, mexicana, panda, rocko, semental, trueno
        
        // given
        List<ConejoModel> lista = Arrays.asList(semental, panda, rocko, trueno, marino, mexicana, enojona);

        // when
        when(conejoRepository.findAll()).thenReturn(lista);
        when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop);
        when(modelMapper.map(any(ConejoModel.class), eq(ConejoDTO.class))).thenAnswer(invocation -> mapModeltoDto(invocation.getArgument(0))); // Obtener cada model y mapear

        // then
        List<ConejoDTO> dtos = conejoService.obtenerConejos();
        assertNotNull(dtos);
        assertEquals(7, dtos.size());
        assertEquals("Enojona", dtos.get(0).getNombre());
        assertEquals("Marino", dtos.get(1).getNombre());
        // ...
        assertEquals("Trueno", dtos.get(6).getNombre());

        verify(conejoRepository, times(1)).findAll();
    }

    @Test
    void testGuardarConejo_Exception_ExistsByNombre(){
        // given
        ConejoDTO sementalDTO = new ConejoDTO(null, null, null,imagen, "Semental", "Macho", null, false, 
        "Primer semental de la granja", null, null, null, null, null, minilop);

        // when
        when(conejoRepository.existsByNombre(anyString())).thenReturn(true);

        // then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> conejoService.guardarConejo(sementalDTO));
        assertNotNull(exception);
        verify(conejoRepository, times(1)).existsByNombre(anyString());
    }

    @Test
    void testGuardarConejo_Exception_ResultUpload(){        
        // given
        ConejoDTO sementalDTO = new ConejoDTO(null, null, null,imagen, "Semental", "Macho", null, false, 
        "Primer semental de la granja", null, null, null, null, null, minilop);

        // when
        when(conejoRepository.existsByNombre(anyString())).thenReturn(false);
        when(archivoUtil.subirImagenCloudinary(any(), anyString(), any())).thenThrow(new RuntimeException("Error al subir la imagen a cloudinary"));
        
        // then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> conejoService.guardarConejo(sementalDTO));
        assertNotNull(exception);
        assertEquals("Error al subir la imagen a cloudinary", exception.getMessage());

        verify(conejoRepository, times(0)).save(any(ConejoModel.class));
    }

    @Test
    void testGuardarConejo_Success(){
        // given
        ConejoDTO sementalDTO = new ConejoDTO(null, null, null,imagen, "Semental", "Macho", null, false, 
        "Primer semental de la granja", null, null, null, null, null, minilop);
        ConejoModel sementalModel = new ConejoModel(null, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, null);

        ConejoModel sementalModelPersis = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);
        ConejoDTO sementalDTOPersis = new ConejoDTO(1L, null, null,null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);
        
        when(conejoRepository.existsByNombre(anyString())).thenReturn(false);
        when(archivoUtil.subirImagenCloudinary(any(), anyString(), any())).thenReturn(mapa);
        when(archivoUtil.getUrlWithPagina(anyString())).thenReturn("https://www.cloudinary.com/imagen.png");
        when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop); // Good before here

        when(modelMapper.map(any(ConejoDTO.class), eq(ConejoModel.class))).thenReturn(sementalModel);
        
        when(conejoRepository.save(any(ConejoModel.class))).thenReturn(sementalModelPersis);

        when(modelMapper.map(any(ConejoModel.class), eq(ConejoDTO.class))).thenReturn(sementalDTOPersis);

        ConejoDTO conejoDTO = conejoService.guardarConejo(sementalDTO);
        assertNotNull(conejoDTO);
        assertEquals(1L, conejoDTO.getId());
        assertEquals("Semental", conejoDTO.getNombre());
        assertNotNull(conejoDTO.getPublicId());
        assertNotNull(conejoDTO.getSecureUrl());
        assertEquals("123abc", conejoDTO.getPublicId());
        assertEquals("https://www.cloudinary.com/imagen.png", conejoDTO.getSecureUrl());
        assertNotNull(conejoDTO.getRaza());
        assertInstanceOf(RazaDTO.class, conejoDTO.getRaza());
        assertEquals("Minilop", conejoDTO.getRaza().getNombre());
    }

    @Test
    void testEditarConejo_Exception_NotFound(){
        // DTO que viene del frontend
        ConejoDTO frontDTO = new ConejoDTO(1L, null, null,imagen, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        when(conejoRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> conejoService.editarConejo(1L, frontDTO));
        assertNotNull(exception);
        assertEquals("El conejo con id "+1L+" no fue encontrado.", exception.getMessage());
    }

    @Test
    void testEditarConejo_Exception_DifferentNameConejoExisting(){
        // DTO que viene del frontend
        ConejoDTO frontDTO = new ConejoDTO(1L, null, null,imagen, "Semental Update", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);
        
        // MODEL original de la BD
        ConejoModel sementalModel = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // DTO del model original de la BD
        ConejoDTO sementalDTO = new ConejoDTO(1L, null, null,null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        when(conejoRepository.findById(anyLong())).thenReturn(Optional.of(sementalModel));
        when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop);
        when(modelMapper.map(any(ConejoModel.class), eq(ConejoDTO.class))).thenReturn(sementalDTO);

        when(conejoRepository.existsByNombre(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> conejoService.editarConejo(1L, frontDTO));
        assertNotNull(exception);
        assertEquals("El conejo "+frontDTO.getNombre()+" ya se encuentra registrado", exception.getMessage());
    }

    /////////////////////////////////////////////////////////////////////////////////
    // First if(conejoDTO.getImagen() != null && !conejoDTO.getImagen().isEmpty()){
    /////////////////////////////////////////////////////////////////////////////////

    @Test
    void testEditarConejo_Exception_DifferentNameAndImage(){ // DifferentNameAndImage -> exception
        // DTO que viene del frontend
        ConejoDTO frontDTO = new ConejoDTO(1L, null, null,imagen, "Semental Update", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        // MODEL original de la BD
        ConejoModel sementalModel = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // DTO del model original de la BD
        ConejoDTO sementalDTO = new ConejoDTO(1L, null, null,null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        // conejoService.obtenerConejoById()
        when(conejoRepository.findById(anyLong())).thenReturn(Optional.of(sementalModel));
        when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop);
        when(modelMapper.map(any(ConejoModel.class), eq(ConejoDTO.class))).thenReturn(sementalDTO);

        // conejoService.existsByNombre()
        when(conejoRepository.existsByNombre(anyString())).thenReturn(false);

        when(archivoUtil.subirImagenCloudinary(any(MockMultipartFile.class), anyString(), any(Optional.class))).thenThrow(new RuntimeException("Error al subir la imagen"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> conejoService.editarConejo(1L, frontDTO));
        assertNotNull(exception);
        assertEquals("Error al subir la imagen", exception.getMessage());
    }

    @Test
    void testEditarConejo_Success_DifferentNameAndImage(){ // DifferentNameAndImage -> success
        // DTO que viene del frontend
        ConejoDTO frontDTO = new ConejoDTO(1L, null, null,imagen, "Semental Update", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        // MODEL original de la BD
        ConejoModel modelOriginal = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // DTO del model original
        ConejoDTO dtoOriginal = new ConejoDTO(1L, null, null,null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        // MODEL que viene del fronted
        ConejoModel frontModel = new ConejoModel(1L, null, null,"Semental Update", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, null);

        // Model persistido
        ConejoModel modelPersis = new ConejoModel(1L, null, null,"Semental Update", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // DTO persistido
        ConejoDTO dtoPersis = new ConejoDTO(1L, null, null, null,"Semental Update", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        // obtenerConejoById()
        when(conejoRepository.findById(anyLong())).thenReturn(Optional.of(modelOriginal));
        when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop);
        when(modelMapper.map(modelOriginal, ConejoDTO.class)).thenReturn(dtoOriginal);

        // existsByNombre()
        when(conejoRepository.existsByNombre("Semental Update")).thenReturn(false);

        // archivoUtil
        when(archivoUtil.subirImagenCloudinary(any(MockMultipartFile.class), anyString(), any(Optional.class))).thenReturn(mapa);
        when(archivoUtil.getUrlWithPagina(anyString())).thenReturn("https://www.cloudinary.com/imagen.png");
        doNothing().when(archivoUtil).eliminarImagenCloudinary(anyString());

        //finally
        // when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop); // Repetido
        when(modelMapper.map(frontDTO, ConejoModel.class)).thenReturn(frontModel);
        when(conejoRepository.save(frontModel)).thenReturn(modelPersis);
        when(modelMapper.map(modelPersis, ConejoDTO.class)).thenReturn(dtoPersis);

        ConejoDTO conejoDTO = conejoService.editarConejo(1L, frontDTO);
        assertNotNull(conejoDTO);
        assertEquals("Semental Update", conejoDTO.getNombre());

        verify(conejoRepository, times(1)).save(eq(frontModel));
        verify(razaClient, atLeast(2)).obtenerRazaPorId(anyLong());
        verify(modelMapper, times(2)).map(any(ConejoModel.class), eq(ConejoDTO.class));
        verify(modelMapper, times(1)).map(any(ConejoDTO.class), eq(ConejoModel.class));
    }

    @Test
    void testEditarConejo_Exception_DifferentNameNullImage(){ // DifferentNameNullImage -> image == null
        // DTO que viene del frontend
        ConejoDTO frontDTO = new ConejoDTO(1L, null, null,null, "Semental Update", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        // MODEL original de la BD
        ConejoModel modelOriginal = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // DTO del model original
        ConejoDTO dtoOriginal = new ConejoDTO(1L, null, null,null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        when(conejoRepository.findById(anyLong())).thenReturn(Optional.of(modelOriginal));
        when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop);
        when(modelMapper.map(modelOriginal, ConejoDTO.class)).thenReturn(dtoOriginal);

        when(conejoRepository.existsByNombre(anyString())).thenReturn(false);

        when(archivoUtil.renombrarImagenCloudinary(anyString(), anyString(), anyString())).thenThrow(new RuntimeException("Ocurrio un error al renombrar la imagen"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> conejoService.editarConejo(1L, frontDTO));
        assertNotNull(exception);
        assertEquals("Ocurrio un error al renombrar la imagen", exception.getMessage());
    }

    @Test
    void testEditarConejo_Exception_DifferentNameEmptyImage(){ // DifferentNameEmptyImage -> image == empty
        // DTO que viene del frontend
        ConejoDTO frontDTO = new ConejoDTO(1L, null, null,imagenVacia, "Semental Update", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        // MODEL original de la BD
        ConejoModel modelOriginal = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // DTO del model original
        ConejoDTO dtoOriginal = new ConejoDTO(1L, null, null,null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        when(conejoRepository.findById(anyLong())).thenReturn(Optional.of(modelOriginal));
        when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop);
        when(modelMapper.map(modelOriginal, ConejoDTO.class)).thenReturn(dtoOriginal);

        when(conejoRepository.existsByNombre(anyString())).thenReturn(false);

        when(archivoUtil.renombrarImagenCloudinary(anyString(), anyString(), anyString())).thenThrow(new RuntimeException("Ocurrio un error al renombrar la imagen"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> conejoService.editarConejo(1L, frontDTO));
        assertNotNull(exception);
        assertEquals("Ocurrio un error al renombrar la imagen", exception.getMessage());
    }

    @Test
    void testEditarConejo_Success_DifferentNameSameImage(){
        // DTO que viene del frontend
        ConejoDTO frontDTO = new ConejoDTO(1L, null, null,null, "Semental Update", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        // MODEL original de la BD
        ConejoModel modelOriginal = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // DTO del model original
        ConejoDTO dtoOriginal = new ConejoDTO(1L, null, null,null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        // MODEL que viene del frontend
        ConejoModel frontModel = new ConejoModel(1L, null, null,"Semental Update", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, null);

        // Model persis
        ConejoModel modelPersis = new ConejoModel(1L, null, null,"Semental Update", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // DTO persis
        ConejoDTO dtoPersis = new ConejoDTO(1L, null, null,null, "Semental Update", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, null);

        when(conejoRepository.findById(anyLong())).thenReturn(Optional.of(modelOriginal));
        when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop);
        when(modelMapper.map(modelOriginal, ConejoDTO.class)).thenReturn(dtoOriginal);

        when(conejoRepository.existsByNombre(anyString())).thenReturn(false);

        when(archivoUtil.renombrarImagenCloudinary(anyString(), anyString(), anyString())).thenReturn(mapa);
        when(archivoUtil.getUrlWithPagina(anyString())).thenReturn("https://www.cloudinary.com/imagen.png");

        when(modelMapper.map(frontDTO, ConejoModel.class)).thenReturn(frontModel);
        when(conejoRepository.save(frontModel)).thenReturn(modelPersis);
        when(modelMapper.map(modelPersis, ConejoDTO.class)).thenReturn(dtoPersis);

        ConejoDTO conejoDTO = conejoService.editarConejo(1L, frontDTO);
        assertNotNull(conejoDTO);
        assertInstanceOf(RazaDTO.class, conejoDTO.getRaza());
        assertEquals("Minilop", conejoDTO.getRaza().getNombre());

        verify(conejoRepository, times(1)).save(frontModel);
        verify(modelMapper, atLeast(2)).map(any(ConejoModel.class), eq(ConejoDTO.class));
        verify(modelMapper, times(1)).map(any(ConejoDTO.class), eq(ConejoModel.class));
    }

    /////////////////////////////////////////////////////////////////////////////////
    // Second if(conejoDTO.getImagen() != null && !conejoDTO.getImagen().isEmpty()){
    /////////////////////////////////////////////////////////////////////////////////

    @Test
    void testEditarConejo_Exception_SameNameDifferentImage(){ // imagen != null -> exception
        // DTO que viene del frontend
        ConejoDTO frontDTO = new ConejoDTO(1L, null, null,imagen, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        // MODEL original de la BD
        ConejoModel modelOriginal = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // DTO del model original
        ConejoDTO dtoOriginal = new ConejoDTO(1L, null, null,null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        when(conejoRepository.findById(anyLong())).thenReturn(Optional.of(modelOriginal));
        when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop);
        when(modelMapper.map(modelOriginal, ConejoDTO.class)).thenReturn(dtoOriginal);

        doThrow(new RuntimeException("Ocurrio un error al eliminar la imagen")).when(archivoUtil).eliminarImagenCloudinary(anyString());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> conejoService.editarConejo(1L, frontDTO));
        assertNotNull(exception);
        assertEquals("Ocurrio un error al eliminar la imagen", exception.getMessage());

        verify(conejoRepository, never()).save(any(ConejoModel.class));
    }

    @Test
    void testEditarConejo_Success_SameNameDifferentImage(){ // imagen != null -> success
        // DTO que viene del frontend
        ConejoDTO frontDTO = new ConejoDTO(1L, null, null,imagen, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        // MODEL original de la BD
        ConejoModel modelOriginal = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // DTO del model original
        ConejoDTO dtoOriginal = new ConejoDTO(1L, null, null,null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        // MODEL que viene del frontend
        ConejoModel frontModel = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, null);

        // Model persis
        ConejoModel modelPersis = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // Model persis
        ConejoDTO dtoPersis = new ConejoDTO(1L, null, null,null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, null);

        when(conejoRepository.findById(anyLong())).thenReturn(Optional.of(modelOriginal));
        when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop);
        when(modelMapper.map(modelOriginal, ConejoDTO.class)).thenReturn(dtoOriginal);

        doNothing().when(archivoUtil).eliminarImagenCloudinary(anyString());
        when(archivoUtil.subirImagenCloudinary(any(MockMultipartFile.class), anyString(), any(Optional.class))).thenReturn(mapa);
        when(archivoUtil.getUrlWithPagina(anyString())).thenReturn("https://www.cloudinary.com/imagen.png");

        when(modelMapper.map(frontDTO, ConejoModel.class)).thenReturn(frontModel);
        when(conejoRepository.save(frontModel)).thenReturn(modelPersis);
        when(modelMapper.map(modelPersis, ConejoDTO.class)).thenReturn(dtoPersis);

        ConejoDTO conejoDTO = conejoService.editarConejo(1L, frontDTO);
        assertNotNull(conejoDTO);
        assertInstanceOf(RazaDTO.class, conejoDTO.getRaza());
        assertEquals("Minilop", conejoDTO.getRaza().getNombre());

        verify(conejoRepository, times(1)).save(any(ConejoModel.class));
        verify(razaClient, atLeast(2)).obtenerRazaPorId(anyLong());
    }

    @Test
    void testEditarConejo_Success_SameNameDifferentImage_nullImage(){ // imagen == null
        // DTO que viene del frontend
        ConejoDTO frontDTO = new ConejoDTO(1L, null, null,null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        // MODEL original de la BD
        ConejoModel modelOriginal = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // DTO del model original
        ConejoDTO dtoOriginal = new ConejoDTO(1L, null, null,null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        // MODEL que viene del frontend
        ConejoModel frontModel = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, null);

        // Model persis
        ConejoModel modelPersis = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // Model persis
        ConejoDTO dtoPersis = new ConejoDTO(1L, null, null,null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, null);

        when(conejoRepository.findById(anyLong())).thenReturn(Optional.of(modelOriginal));
        when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop);
        when(modelMapper.map(modelOriginal, ConejoDTO.class)).thenReturn(dtoOriginal);

        when(modelMapper.map(frontDTO, ConejoModel.class)).thenReturn(frontModel);
        when(conejoRepository.save(frontModel)).thenReturn(modelPersis);
        when(modelMapper.map(modelPersis, ConejoDTO.class)).thenReturn(dtoPersis);

        ConejoDTO conejoDTO = conejoService.editarConejo(1L, frontDTO);
        assertNotNull(conejoDTO);
        assertInstanceOf(RazaDTO.class, conejoDTO.getRaza());
        assertEquals("Minilop", conejoDTO.getRaza().getNombre());

        verify(conejoRepository, times(1)).save(any(ConejoModel.class));
        verify(razaClient, atLeast(2)).obtenerRazaPorId(anyLong());
    }

    @Test
    void testEditarConejo_Success_SameNameDifferentImage_EmptyImage(){ // imagen == empty
        MockMultipartFile emptyImage = new MockMultipartFile("image", new byte[0]);

        // DTO que viene del frontend
        ConejoDTO frontDTO = new ConejoDTO(1L, null, null,emptyImage, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        // MODEL original de la BD
        ConejoModel modelOriginal = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // DTO del model original
        ConejoDTO dtoOriginal = new ConejoDTO(1L, null, null,null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, minilop);

        // MODEL que viene del frontend
        ConejoModel frontModel = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, null);

        // Model persis
        ConejoModel modelPersis = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // Model persis
        ConejoDTO dtoPersis = new ConejoDTO(1L, null, null,null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, null);

        when(conejoRepository.findById(anyLong())).thenReturn(Optional.of(modelOriginal));
        when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop);
        when(modelMapper.map(modelOriginal, ConejoDTO.class)).thenReturn(dtoOriginal);

        when(modelMapper.map(frontDTO, ConejoModel.class)).thenReturn(frontModel);
        when(conejoRepository.save(frontModel)).thenReturn(modelPersis);
        when(modelMapper.map(modelPersis, ConejoDTO.class)).thenReturn(dtoPersis);

        ConejoDTO conejoDTO = conejoService.editarConejo(1L, frontDTO);
        assertNotNull(conejoDTO);
        assertInstanceOf(RazaDTO.class, conejoDTO.getRaza());
        assertEquals("Minilop", conejoDTO.getRaza().getNombre());

        verify(conejoRepository, times(1)).save(any(ConejoModel.class));
        verify(razaClient, atLeast(2)).obtenerRazaPorId(anyLong());
    }

    /////////////////////////////////////////////////////////////////////////////////
    // Fin Second if(conejoDTO.getImagen() != null && !conejoDTO.getImagen().isEmpty()){
    /////////////////////////////////////////////////////////////////////////////////

    @Test
    void testEliminarConejoById_Exception_NotFound(){
        when(conejoRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> conejoService.eliminarConejoById(1L));
        assertNotNull(exception);
        assertEquals("El conejo con id "+1L+" no fue encontrado.", exception.getMessage());
    }

    @Test
    void testEliminarConejoById_Exception_MachoEnUso(){
        // given
        ConejoModel semental = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // when
        when(conejoRepository.findById(anyLong())).thenReturn(Optional.of(semental));
        when(montaRepository.existsByMacho(semental)).thenReturn(true);

        // then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> conejoService.eliminarConejoById(1L));
        assertNotNull(exception);
        assertEquals(semental.getNombre()+" está en uso actualmente, puede darlo de baja o desvincularlo para eliminar.", exception.getMessage());
    }

    @Test
    void testEliminarConejoById_Exception_HembraEnUso(){
        // given
        ConejoModel enojona = new ConejoModel(1L, null, null,"Enojona", "Hembra", null, false, 
        "Única mordelona en la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // when
        when(conejoRepository.findById(anyLong())).thenReturn(Optional.of(enojona));
        when(montaRepository.existsByHembra(enojona)).thenReturn(true);

        // then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> conejoService.eliminarConejoById(1L));
        assertNotNull(exception);
        assertEquals(enojona.getNombre()+" está en uso actualmente, puede darlo de baja o desvincularlo para eliminar.", exception.getMessage());
    }

    @Test
    void testEliminarConejoById_Exception_EliminarImagenCloudinary() throws Exception{
        // given
        ConejoModel semental = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        // when
        when(conejoRepository.findById(anyLong())).thenReturn(Optional.of(semental));
        when(montaRepository.existsByMacho(semental)).thenReturn(false);
        doThrow(new RuntimeException("Ocurrio un error al eliminar la imagen")).when(archivoUtil).eliminarImagenCloudinary(anyString());

        // then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> conejoService.eliminarConejoById(1L));
        assertNotNull(exception);
        assertEquals("Ocurrio un error al eliminar la imagen", exception.getMessage());

        verify(conejoRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void testEliminarConejoById_Exception_EliminarConejo(){
        ConejoModel semental = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        when(conejoRepository.findById(anyLong())).thenReturn(Optional.of(semental));
        when(montaRepository.existsByMacho(semental)).thenReturn(false);
        doNothing().when(archivoUtil).eliminarImagenCloudinary(anyString());
        
        doThrow(new RuntimeException()).when(conejoRepository).deleteById(anyLong()); // return void
        // doNothing().when(conejoRepository).deleteById(anyLong());
        // doThrow(new RuntimeException("Ocurrio un error al eliminar el conejo")).when(conejoRepository).deleteById(anyLong());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> conejoService.eliminarConejoById(1L));
        assertNotNull(exception);
        assertEquals("Ocurrio un error el eliminar el conejo de la base de datos.", exception.getMessage());

        verify(conejoRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void testEliminarConejoById_Success(){
        ConejoModel semental = new ConejoModel(1L, null, null,"Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://www.cloudinary.com/imagen.png", null, null, null, 1L);

        when(conejoRepository.findById(anyLong())).thenReturn(Optional.of(semental));
        when(montaRepository.existsByMacho(semental)).thenReturn(false);
        doNothing().when(archivoUtil).eliminarImagenCloudinary(anyString());
        doNothing().when(conejoRepository).deleteById(anyLong());

        boolean eliminado = conejoService.eliminarConejoById(1L);
        assertTrue(eliminado);
        verify(archivoUtil, times(1)).eliminarImagenCloudinary("123abc");
        verify(conejoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testObtenerConejosPorSexo(){
        List<ConejoModel> machos = Arrays.asList(semental, rocko, trueno, marino);

        when(conejoRepository.findBySexoIgnoreCase(anyString())).thenReturn(machos);
        when(razaClient.obtenerRazaPorId(anyLong())).thenReturn(minilop);
        when(modelMapper.map(any(ConejoModel.class), eq(ConejoDTO.class))).thenAnswer(invocation -> mapModeltoDto(invocation.getArgument(0)));

        List<ConejoDTO> dtos = conejoService.obtenerConejosPorSexo("Macho");

        assertNotNull(dtos);
        assertEquals(4, dtos.size());
        assertEquals("Marino", dtos.get(0).getNombre());
        assertEquals("Rocko", dtos.get(1).getNombre());
        assertEquals("Semental", dtos.get(2).getNombre());
        assertEquals("Trueno", dtos.get(3).getNombre());

        verify(modelMapper, atMost(4)).map(any(ConejoModel.class), eq(ConejoDTO.class));
    }

    @Test
    void testObtenerConejosActivosPorSexo(){
        List<ConejoModel> machosActivos = Arrays.asList(rocko, trueno, marino);

        when(conejoRepository.findBySexoIgnoreCaseAndActivoTrue(anyString())).thenReturn(machosActivos);
        when(modelMapper.map(any(ConejoModel.class), eq(ConejoDTO.class))).thenAnswer(invocation -> mapModeltoDto(invocation.getArgument(0)));

        List<ConejoDTO> dtos = conejoService.obtenerConejosActivosPorSexo("Macho");
        
        assertNotNull(dtos);
        assertEquals(3, dtos.size());
        assertEquals("Marino", dtos.get(0).getNombre());
        assertEquals("Rocko", dtos.get(1).getNombre());
        assertEquals("Trueno", dtos.get(2).getNombre());

        verify(modelMapper, atMost(3)).map(any(ConejoModel.class), eq(ConejoDTO.class));
    }

    @Test
    void testExistsByNombre(){
        when(conejoRepository.existsByNombre(anyString())).thenReturn(true);

        boolean existe = conejoService.existsByNombre("Semental");
        assertTrue(existe);

        verify(conejoRepository, atMost(1)).existsByNombre(anyString());
    }

    @Test
    void testExistsById(){
        when(conejoRepository.existsById(anyLong())).thenReturn(true);

        boolean existe = conejoService.existsById(1L);
        assertTrue(existe);

        verify(conejoRepository, atMost(1)).existsById(anyLong());
    }

    @Test
    void testExistsByRazaId(){
        when(conejoRepository.existsByRazaId(anyLong())).thenReturn(true);

        boolean existe = conejoService.existsByRazaId(1L);
        assertTrue(existe);

        verify(conejoRepository, atMost(1)).existsByRazaId(anyLong());
    }
}
