package com.project.artists.config;

import com.project.artists.dto.request.AlbumRequestDTO;
import com.project.artists.dto.request.ArtistRequestDTO;
import com.project.artists.dto.response.AlbumResponseDTO;
import com.project.artists.dto.response.ArtistResponseDTO;
import com.project.artists.entity.User;
import com.project.artists.repository.AlbumRepository;
import com.project.artists.repository.ArtistRepository;
import com.project.artists.repository.UserRepository;
import com.project.artists.service.AlbumService;
import com.project.artists.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Override
    public void run(String... args) {
        createDefaultAdmin();
        seedArtistsAlbumsAndUploadCoversWithRetry();
    }

    private void createDefaultAdmin() {
        String username = "admin";
        String email = "admin@artists.com";
        String rawPassword = "admin123";

        if (userRepository.findByUsername(username).isEmpty()) {
            User admin = new User();
            admin.setUsername(username);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(rawPassword));

            userRepository.save(admin);
        }
    }

    private void seedArtistsAlbumsAndUploadCoversWithRetry() {
        try {
            albumService.uploadCovers(1L, List.of(loadAsMultipart("seed-covers/harakiri.jpg", "harakiri.jpg")));

            albumService.uploadCovers(2L, List.of(loadAsMultipart("seed-covers/post_traumatic.jpg", "post_traumatic.jpg")));

            albumService.uploadCovers(3L, List.of(loadAsMultipart("seed-covers/bem_sertanejo.jpg", "bem_sertanejo.jpg")));

            albumService.uploadCovers(4L, List.of(loadAsMultipart("seed-covers/na_balada.jpg", "na_balada.jpg")));

            albumService.uploadCovers(5L, List.of(loadAsMultipart("seed-covers/appetite_for_destruction.jpg", "appetite_for_destruction.jpg")));

            albumService.uploadCovers(6L, List.of(loadAsMultipart("seed-covers/use_your_Illusion.jpg", "use_your_Illusion.jpg")));

            albumService.uploadCovers(7L, List.of(loadAsMultipart("seed-covers/use_your_IllusionII.jpg", "use_your_IllusionII.jpg")));

            albumService.uploadCovers(8L, List.of(loadAsMultipart("seed-covers/greatest_hits.jpg", "greatest_hits.jpg")));

            albumService.uploadCovers(9L, List.of(loadAsMultipart("seed-covers/the_rise_and_fall.jpeg", "the_rise_and_fall.jpeg")));

            albumService.uploadCovers(10L, List.of(loadAsMultipart("seed-covers/bandido.jpeg", "bandido.jpeg")));

            albumService.uploadCovers(11L, List.of(loadAsMultipart("seed-covers/pecado.jpg", "pecado.jpg")));

            albumService.uploadCovers(13L, List.of(loadAsMultipart("seed-covers/olho_de_farol.jpg", "olho_de_farol.jpg")));

            albumService.uploadCovers(12L, List.of(loadAsMultipart("seed-covers/ney_mato_grosso.jpeg", "ney_mato_grosso.jpeg")));

            albumService.uploadCovers(14L, List.of(loadAsMultipart("seed-covers/rr.jpeg", "rr.jpeg")));

            albumService.uploadCovers(15L, List.of(loadAsMultipart("seed-covers/motomami_1.jpg", "motmomami_1.jpg")));

            albumService.uploadCovers(15L, List.of(loadAsMultipart("seed-covers/motomami_2.jpeg", "motomami_2.jpeg")));

            albumService.uploadCovers(16L, List.of(loadAsMultipart("seed-covers/lux.png", "lux.png")));

            albumService.uploadCovers(17L, List.of(loadAsMultipart("seed-covers/illinois.jpg", "illinois.jpg")));

            albumService.uploadCovers(18L, List.of(loadAsMultipart("seed-covers/carrie_and_lowell.jpg", "carrie_and_lowell.jpg")));

            albumService.uploadCovers(18L, List.of(loadAsMultipart("seed-covers/carrie_and_lowell2.jpg", "carrie_and_lowell2.jpg")));

            albumService.uploadCovers(19L, List.of(loadAsMultipart("seed-covers/monomania.jpeg", "monomania.jpeg")));

            albumService.uploadCovers(20L, List.of(loadAsMultipart("seed-covers/problema_meu.jpeg", "problema_meu.jpeg")));

            albumService.uploadCovers(21L, List.of(loadAsMultipart("seed-covers/doma.jpeg", "doma.jpeg")));

            albumService.uploadCovers(22L, List.of(loadAsMultipart("seed-covers/estradeiro.jpeg", "estradeiro.jpeg")));

            albumService.uploadCovers(23L, List.of(loadAsMultipart("seed-covers/donde_estan_los_ladrones.jpg", "donde_estan_los_ladrones.jpg")));

            albumService.uploadCovers(24L, List.of(loadAsMultipart("seed-covers/pies_descalzos.jpg", "pies_descalzos.jpg")));

            albumService.uploadCovers(25L, List.of(loadAsMultipart("seed-covers/mil_coisas_invisiveis.jpg", "mil_coisas_invisiveis.jpg")));

            albumService.uploadCovers(26L, List.of(loadAsMultipart("seed-covers/gal_tropical.jpg", "gal_tropical.jpg")));

            albumService.uploadCovers(27L, List.of(loadAsMultipart("seed-covers/spanish_leather.jpeg", "spanish_leather.jpg")));

            albumService.uploadCovers(28L, List.of(loadAsMultipart("seed-covers/revolver.jpeg", "revolver.jpeg")));

            albumService.uploadCovers(28L, List.of(loadAsMultipart("seed-covers/revolver.jpeg", "revolver.jpeg")));

            albumService.uploadCovers(29L, List.of(loadAsMultipart("seed-covers/abbey_road.jpeg", "abbey_road.jpeg")));

            albumService.uploadCovers(30L, List.of(loadAsMultipart("seed-covers/fetch_the_bolt_cutters.png", "fetch_the_bolt_cutters.png")));

        } catch (Exception e) {
        }
    }


    private void safeCleanup() {
        try {
            albumRepository.deleteAll();
        } catch (Exception ignored) {
        }
        try {
            artistRepository.deleteAll();
        } catch (Exception ignored) {
        }
    }

    private ArtistRequestDTO buildArtist(String name, String bio) {
        ArtistRequestDTO dto = new ArtistRequestDTO();
        dto.setName(name);
        dto.setBio(bio);
        return dto;
    }

    private AlbumRequestDTO buildAlbum(String title, int releaseYear, Long artistId) {
        AlbumRequestDTO dto = new AlbumRequestDTO();
        dto.setTitle(title);
        dto.setReleaseYear(releaseYear);
        dto.setArtistIds(List.of(artistId));
        return dto;
    }

    private MultipartFile loadAsMultipart(String classpathFile, String originalFilename) throws Exception {
        ClassPathResource resource = new ClassPathResource(classpathFile);

        if (!resource.exists()) {
            throw new RuntimeException("Arquivo não encontrado em: " + classpathFile);
        }

        try (InputStream is = resource.getInputStream()) {
            return new MockMultipartFile(
                    "files", 
                    originalFilename,
                    guessContentType(originalFilename),
                    is.readAllBytes()
            );
        }
    }

    private String guessContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpeg")) return "image/jpg";
        return "image/jpeg";
    }
}
