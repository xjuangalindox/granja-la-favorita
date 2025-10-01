package com.favorita.articulos.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.favorita.articulos.clients.ArticuloVentaClient;
import com.favorita.articulos.controller.dto.ArticuloDTO;
import com.favorita.articulos.model.ArticuloModel;
import com.favorita.articulos.repository.IArticuloRepository;
import com.favorita.articulos.services.ArticuloServiceImpl;
import com.favorita.articulos.util.ArchivoUtil;

@ExtendWith(MockitoExtension.class)
public class ArticuloServiceTest {
    
    @Mock
    private IArticuloRepository articuloRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ArchivoUtil archivoUtil;

    @Mock
    private ArticuloVentaClient articuloVentaClient;

    @InjectMocks
    private ArticuloServiceImpl articuloService;

    ArticuloModel campiconejo, heno, aserrin;

    @BeforeEach
    void setup(){
        campiconejo = new ArticuloModel(1L, "Campiconejo", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");
        
        heno = new ArticuloModel(2L, "Heno de avena con grano", "Complemento de comida, ideal para desgastar los dientes y mantener la digestión saludable.", 
        "Bolsa (200 g)", 20.0, "public_id", "secure_url");
        
        aserrin = new ArticuloModel(3L, "Aserrín", "Mantiene el lugar limpio, ayuda a evitar malos olores y previene la presencia de moscas.", 
        "KG", 20.0, "public_id", "secure_url");
    }

    @Test
    void testObtenerArticulos(){
        // given
        List<ArticuloModel> lista = new ArrayList<>();
        lista.add(campiconejo);
        lista.add(heno);
        lista.add(aserrin);
        
        given(articuloRepository.findAll()).willReturn(lista);

        // when
        List<ArticuloDTO> articulos = articuloService.obtenerArticulos();

        // then
        assertNotNull(articulos);
        assertEquals(3, articulos.size());
    }

    @Test
    void testObtenerArticuloPorId(){
        // given
        ArticuloModel model = new ArticuloModel(1L, "Campiconejo", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");
        ArticuloDTO dto = new ArticuloDTO(1L, "Campiconejo", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, null, "public_id", "secure_url");

        given(articuloRepository.findById(1L)).willReturn(Optional.of(model));
        given(modelMapper.map(model, ArticuloDTO.class)).willReturn(dto);

        // when
        Optional<ArticuloDTO> result = articuloService.obtenerArticuloPorId(1L);
        
        // then
        assertTrue(result.isPresent());
        assertEquals("Campiconejo", result.get().getNombre());
    }

    @Test
    void testEliminarArticuloPorId(){
        // given
        ArticuloModel model = new ArticuloModel(1L, "Campiconejo", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");

        when(articuloRepository.findById(anyLong())).thenReturn(Optional.of(model));
        when(articuloVentaClient.existsByArticuloId(anyLong())).thenReturn(false);
        doNothing().when(archivoUtil).eliminarImagenCloudinary(anyString());
        doNothing().when(articuloRepository).deleteById(anyLong());

        // when
        articuloService.eliminarArticuloPorId(1L);

        // then
        verify(articuloRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarArticuloPorId_NotFound(){
        // given
        when(articuloRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> articuloService.eliminarArticuloPorId(1L));

        // then
        verify(articuloRepository, never()).deleteById(anyLong());
    }

    @Test
    void testEliminarArticuloPorId_AlreadyExists(){
        // given
        ArticuloModel model = new ArticuloModel(1L, "Campiconejo", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");
        when(articuloRepository.findById(anyLong())).thenReturn(Optional.of(model));
        when(articuloVentaClient.existsByArticuloId(anyLong())).thenReturn(true);

        // when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> articuloService.eliminarArticuloPorId(1L));

        // then
        verify(articuloRepository, times(0)).deleteById(1L);
    }

    @Test
    void guardarArticulo_Success(){
        // given
        MockMultipartFile imagen = new MockMultipartFile("imagen", new byte[]{1, 2, 3});

        ArticuloDTO dto = new ArticuloDTO(null, "Campiconejo", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, imagen, "public_id", "secure_url");
        ArticuloModel model = new ArticuloModel(null, "Campiconejo", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");

        ArticuloModel modelSaved = new ArticuloModel(1L, "Campiconejo", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");
        ArticuloDTO dtoSaved = new ArticuloDTO(1L, "Campiconejo", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, imagen, "public_id", "secure_url");

        when(articuloRepository.existsByNombre(anyString())).thenReturn(false);

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("public_id", "abc123");
        when(archivoUtil.subirImagenCloudinary(any(), anyString(), any())).thenReturn(uploadResult);
        when(archivoUtil.getUrlWithPagina(anyString())).thenReturn("secure_url");

        when(modelMapper.map(dto, ArticuloModel.class)).thenReturn(model);
        when(articuloRepository.save(model)).thenReturn(modelSaved);
        when(modelMapper.map(modelSaved, ArticuloDTO.class)).thenReturn(dtoSaved);

        // when
        ArticuloDTO articuloDTO = articuloService.guardarArticulo(dto);

        // then
        assertNotNull(articuloDTO);
        assertEquals("Campiconejo", articuloDTO.getNombre());
        assertEquals(20.0, articuloDTO.getPrecio());
    }

    @Test
    void testGuardarArticulo_ExistsByNombre(){
        MockMultipartFile imagen = new MockMultipartFile("imagen", new byte[]{1, 2, 3});

        ArticuloDTO dto = new ArticuloDTO(null, "Campiconejo", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, imagen, "public_id", "secure_url");

        when(articuloRepository.existsByNombre(dto.getNombre())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> articuloService.guardarArticulo(dto));

        verify(articuloRepository, never()).save(any());
    }

    @Test
    void testGuardarArticulo_ErrorSaveImage(){
        MockMultipartFile imagen = new MockMultipartFile("imagen", new byte[]{1, 2, 3});

        ArticuloDTO dto = new ArticuloDTO(null, "Campiconejo", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, imagen, "public_id", "secure_url");

        when(articuloRepository.existsByNombre(dto.getNombre())).thenReturn(false);
        when(archivoUtil.subirImagenCloudinary(any(), anyString(), any())).thenThrow(new RuntimeException("Error al subir la imagen"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> articuloService.guardarArticulo(dto));

        assertEquals("Error al subir la imagen", exception.getMessage());
        verify(articuloRepository, never()).save(any());
    }

    @Test
    void testEditarArticulo_SuccessSameNameWithImage(){
        // given
        MockMultipartFile imagen = new MockMultipartFile("imagen", new byte[]{1, 2, 3});

        ArticuloDTO dto = new ArticuloDTO(1L, "Campiconejo", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, imagen, "public_id", "secure_url");
        ArticuloModel model = new ArticuloModel(1L, "Campiconejo", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");

        // when
        when(articuloRepository.findById(dto.getId())).thenReturn(Optional.of(model));
        doNothing().when(archivoUtil).eliminarImagenCloudinary(anyString());
        
        Map<String, Object> resultUpload = new HashMap<>();
        resultUpload.put("public_id", "123abc");
        resultUpload.put("secure_url", "https://image.jpg");
        when(archivoUtil.subirImagenCloudinary(any(), anyString(), any())).thenReturn(resultUpload);
        when(archivoUtil.getUrlWithPagina(anyString())).thenReturn("secure_url");

        when(articuloRepository.save(model)).thenReturn(model);
        when(modelMapper.map(model, ArticuloDTO.class)).thenReturn(dto);

        // then
        ArticuloDTO articuloDTO = articuloService.editarArticulo(dto.getId(), dto);

        // verify
        assertNotNull(articuloDTO);
        verify(articuloRepository, times(1)).save(model);
    }

    @Test
    void testEditarArticulo_SuccessDiferentNameWithImage(){
        // given
        MockMultipartFile imagen = new MockMultipartFile("imagen", new byte[]{1, 2, 3});

        ArticuloDTO dto = new ArticuloDTO(1L, "Aserrin", "Mantiene el lugar limpio, ayuda a evitar malos olores y previene la presencia de moscas.", 
        "KG", 20.0, imagen, "public_id", "secure_url");
        ArticuloModel model = new ArticuloModel(1L, "Alimento", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");
        
        ArticuloModel modelSaved = new ArticuloModel(1L, "Aserrin", "Mantiene el lugar limpio, ayuda a evitar malos olores y previene la presencia de moscas.", 
        "KG", 20.0, "public_id", "secure_url");
        ArticuloDTO dtoSaved = new ArticuloDTO(1L, "Aserrin", "Mantiene el lugar limpio, ayuda a evitar malos olores y previene la presencia de moscas.", 
        "KG", 20.0, imagen, "public_id", "secure_url");

        when(articuloRepository.findById(dto.getId())).thenReturn(Optional.of(model)); // encontrado
        when(articuloRepository.existsByNombre(dto.getNombre())).thenReturn(false); // no registrado
        doNothing().when(archivoUtil).eliminarImagenCloudinary(anyString());

        Map<String, Object> resultUpload = new HashMap<>();
        resultUpload.put("public_id", "123abc");
        resultUpload.put("secure_url", "https://image.jpg");
        when(archivoUtil.subirImagenCloudinary(any(), anyString(), any())).thenReturn(resultUpload);
        when(archivoUtil.getUrlWithPagina(anyString())).thenReturn("secure_url");

        when(articuloRepository.save(any(ArticuloModel.class))).thenReturn(modelSaved);
        when(modelMapper.map(modelSaved, ArticuloDTO.class)).thenReturn(dtoSaved);

        // when
        ArticuloDTO articuloDTO = articuloService.editarArticulo(dto.getId(), dto);

        // then
        assertNotNull(articuloDTO);
        verify(articuloRepository, times(1)).save(any(ArticuloModel.class));
    }

    @Test
    void testEditarArticulo_SuccessDiferentNameWithoutImage(){
        // given
        ArticuloDTO dto = new ArticuloDTO(1L, "Aserrin", "Mantiene el lugar limpio, ayuda a evitar malos olores y previene la presencia de moscas.", 
        "KG", 20.0, null, "public_id", "secure_url");
        ArticuloModel model = new ArticuloModel(1L, "Alimento", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");
        
        ArticuloModel modelSaved = new ArticuloModel(1L, "Aserrin", "Mantiene el lugar limpio, ayuda a evitar malos olores y previene la presencia de moscas.", 
        "KG", 20.0, "public_id", "secure_url");
        ArticuloDTO dtoSaved = new ArticuloDTO(1L, "Aserrin", "Mantiene el lugar limpio, ayuda a evitar malos olores y previene la presencia de moscas.", 
        "KG", 20.0, null, "public_id", "secure_url");

        when(articuloRepository.findById(dto.getId())).thenReturn(Optional.of(model));
        when(articuloRepository.existsByNombre(anyString())).thenReturn(false);
        
        Map<String, Object> resultRename = new HashMap<>();
        resultRename.put("public_id", "123abc");
        when(archivoUtil.renombrarImagenCloudinary(anyString(), anyString(), anyString())).thenReturn(resultRename);
        when(archivoUtil.getUrlWithPagina(anyString())).thenReturn("https://imagen.jpg");

        when(articuloRepository.save(model)).thenReturn(modelSaved);
        when(modelMapper.map(modelSaved, ArticuloDTO.class)).thenReturn(dtoSaved);

        // when
        ArticuloDTO articuloDTO = articuloService.editarArticulo(dto.getId(), dto);

        // then
        assertNotNull(articuloDTO);
        assertInstanceOf(ArticuloDTO.class, articuloDTO);
        assertEquals("Aserrin", articuloDTO.getNombre());
        verify(articuloRepository, times(1)).save(model);
        Optional<ArticuloModel> opt = articuloRepository.findById(articuloDTO.getId());
        assertTrue(opt.isPresent());
    }

    @Test
    void testEditarArticulo_Error_NotFound(){
        when(articuloRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> articuloService.editarArticulo(1L, any(ArticuloDTO.class)));

        verify(articuloRepository, never()).save(any(ArticuloModel.class));
    }

    @Test
    void testEditarArticulo_Error_Existing(){
        ArticuloDTO dto = new ArticuloDTO(1L, "Aserrin", "Mantiene el lugar limpio, ayuda a evitar malos olores y previene la presencia de moscas.", 
        "KG", 20.0, null, "public_id", "secure_url");
        ArticuloModel model = new ArticuloModel(1L, "Alimento", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");

        when(articuloRepository.findById(dto.getId())).thenReturn(Optional.of(model));
        when(articuloRepository.existsByNombre(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> articuloService.editarArticulo(dto.getId(), dto));

        assertEquals("El articulo "+dto.getNombre()+" ya se encuentra registrado.", exception.getMessage());
        verify(articuloRepository, never()).save(any());
    }

    @Test
    void testEditarArticulo_Error_DiferentNameImageUpload(){
        MockMultipartFile imagen = new MockMultipartFile("imagen", new byte[]{1, 2, 3});

        ArticuloDTO dto = new ArticuloDTO(1L, "Aserrin", "Mantiene el lugar limpio, ayuda a evitar malos olores y previene la presencia de moscas.", 
        "KG", 20.0, imagen, "public_id", "secure_url");
        ArticuloModel model = new ArticuloModel(1L, "Alimento", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");
        
        when(articuloRepository.findById(dto.getId())).thenReturn(Optional.of(model));
        when(articuloRepository.existsByNombre(anyString())).thenReturn(false);
        doNothing().when(archivoUtil).eliminarImagenCloudinary(anyString());
        
        when(archivoUtil.subirImagenCloudinary(any(), anyString(), any())).thenThrow(new RuntimeException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> articuloService.editarArticulo(dto.getId(), dto));
        assertNotNull(exception);

        verify(articuloRepository, never()).save(any());
    }

    @Test
    void testEditarArticulo_Error_RenameImage(){
        ArticuloDTO dto = new ArticuloDTO(1L, "Aserrin", "Mantiene el lugar limpio, ayuda a evitar malos olores y previene la presencia de moscas.", 
        "KG", 20.0, null, "public_id", "secure_url");
        ArticuloModel model = new ArticuloModel(1L, "Alimento", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");

        when(articuloRepository.findById(dto.getId())).thenReturn(Optional.of(model));
        when(articuloRepository.existsByNombre(anyString())).thenReturn(false);

        when(archivoUtil.renombrarImagenCloudinary(anyString(), anyString(), anyString())).thenThrow(new RuntimeException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> articuloService.editarArticulo(dto.getId(), dto));
        assertNotNull(exception);
        verify(articuloRepository, never()).save(any());
    }

    @Test
    void testEditarArticulo_Error_SameNameImageUpload(){
        MockMultipartFile imagen = new MockMultipartFile("imagen", new byte[]{1, 2, 3});

        ArticuloDTO dto = new ArticuloDTO(1L, "Alimento", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, imagen, "public_id", "secure_url");
        ArticuloModel model = new ArticuloModel(1L, "Alimento", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");

        when(articuloRepository.findById(dto.getId())).thenReturn(Optional.of(model));
        doNothing().when(archivoUtil).eliminarImagenCloudinary(anyString());

        when(archivoUtil.subirImagenCloudinary(any(), anyString(), any())).thenThrow(new RuntimeException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> articuloService.editarArticulo(dto.getId(), dto));

        assertNotNull(exception);
        verify(articuloRepository, never()).save(any());
    }

    @Test
    void testEditarArticulo_If_DiferentNameWithoutImage(){
        MockMultipartFile imagenVacia = new MockMultipartFile("imagenVacia", new byte[0]);

        ArticuloDTO dto = new ArticuloDTO(1L, "Aserrin", "Mantiene el lugar limpio, ayuda a evitar malos olores y previene la presencia de moscas.", 
        "KG", 20.0, imagenVacia, "public_id", "secure_url");
        ArticuloModel model = new ArticuloModel(1L, "Alimento", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");

        ArticuloModel modelSaved = new ArticuloModel(1L, "Aserrin", "Mantiene el lugar limpio, ayuda a evitar malos olores y previene la presencia de moscas.", 
        "KG", 20.0, "public_id", "secure_url");
        ArticuloDTO dtoSaved = new ArticuloDTO(1L, "Aserrin", "Mantiene el lugar limpio, ayuda a evitar malos olores y previene la presencia de moscas.", 
        "KG", 20.0, null, "public_id", "secure_url");

        when(articuloRepository.findById(dto.getId())).thenReturn(Optional.of(model));
        when(articuloRepository.existsByNombre(anyString())).thenReturn(false);

        Map<String, Object> resultRename = new HashMap<>();
        resultRename.put("public_id", "123abc");
        when(archivoUtil.renombrarImagenCloudinary(anyString(), anyString(), anyString())).thenReturn(resultRename);
        when(archivoUtil.getUrlWithPagina(anyString())).thenReturn("https://imagen.jpg");

        when(articuloRepository.save(model)).thenReturn(modelSaved);
        when(modelMapper.map(modelSaved, ArticuloDTO.class)).thenReturn(dtoSaved);

        ArticuloDTO articuloDTO = articuloService.editarArticulo(dto.getId(), dto);

        assertNotNull(articuloDTO);
        verify(articuloRepository, times(1)).save(any());
    }

    @Test
    void testEditarArticulo_If_SameNameWithoutImage(){
        MockMultipartFile imagenVacia = new MockMultipartFile("imagenVacia", new byte[0]);

        ArticuloDTO dto = new ArticuloDTO(1L, "Alimento", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, imagenVacia, "public_id", "secure_url");
        ArticuloModel model = new ArticuloModel(1L, "Alimento", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");

        ArticuloModel modelSaved = new ArticuloModel(1L, "Alimento", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");
        ArticuloDTO dtoSaved = new ArticuloDTO(1L, "Alimento", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, null, "public_id", "secure_url");

        when(articuloRepository.findById(dto.getId())).thenReturn(Optional.of(model));
        
        when(articuloRepository.save(model)).thenReturn(modelSaved);
        when(modelMapper.map(modelSaved, ArticuloDTO.class)).thenReturn(dtoSaved);

        ArticuloDTO articuloDTO = articuloService.editarArticulo(dto.getId(), dto);

        assertNotNull(articuloDTO);
        verify(articuloRepository, times(1)).save(any());
    }

    @Test
    void testEditarArticulo_If_SameNameNullImage(){
        // MockMultipartFile imagenVacia = new MockMultipartFile("imagenVacia", new byte[0]);

        ArticuloDTO dto = new ArticuloDTO(1L, "Alimento", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, null, "public_id", "secure_url");
        ArticuloModel model = new ArticuloModel(1L, "Alimento", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");

        ArticuloModel modelSaved = new ArticuloModel(1L, "Alimento", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, "public_id", "secure_url");
        ArticuloDTO dtoSaved = new ArticuloDTO(1L, "Alimento", "Alimento diario completo, balanceado para el crecimiento y mantenimiento de tus conejos.", 
        "KG", 20.0, null, "public_id", "secure_url");

        when(articuloRepository.findById(dto.getId())).thenReturn(Optional.of(model));
        
        when(articuloRepository.save(model)).thenReturn(modelSaved);
        when(modelMapper.map(modelSaved, ArticuloDTO.class)).thenReturn(dtoSaved);

        ArticuloDTO articuloDTO = articuloService.editarArticulo(dto.getId(), dto);

        assertNotNull(articuloDTO);
        verify(articuloRepository, times(1)).save(any());
    }
}
