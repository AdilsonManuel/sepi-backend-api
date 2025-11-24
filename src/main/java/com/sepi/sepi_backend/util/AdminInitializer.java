///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.sepi.sepi_backend.util;
//
//import com.sepi.sepi_backend.entity.Localidade;
//import com.sepi.sepi_backend.entity.Usuario;
//import com.sepi.sepi_backend.enums.StatusUsuario;
//import com.sepi.sepi_backend.enums.TipoDocumento;
//import com.sepi.sepi_backend.enums.TipoUsuario;
//import com.sepi.sepi_backend.repository.LocalidadeRepository;
//import com.sepi.sepi_backend.repository.UsuarioRepository;
//import java.time.LocalDate;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
///**
// *
// * @author azm
// */
//@Component
//@RequiredArgsConstructor
//public class AdminInitializer implements CommandLineRunner
//{
//
//    private final UsuarioRepository usuarioRepository;
//    private final LocalidadeRepository localidadeRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run (String... args) throws Exception
//    {
//        String emailAdmin = "admin@sepi.co.ao";
//
//        if (usuarioRepository.findByEmail(emailAdmin).isEmpty())
//        {
//            // Garante que existe uma localidade (necessária pela entidade Usuario)
//            Localidade localidade = localidadeRepository.findById("LA").orElse(null);
//            if (localidade == null)
//            {
//                // Se o LocalidadeInitializer ainda não correu, cria uma dummy
//                localidade = new Localidade("LA", "Luanda", null, null);
//                localidadeRepository.save(localidade);
//            }
//
//            Usuario admin = new Usuario(
//                    "Administrador do Sistema",
//                    emailAdmin,
//                    passwordEncoder.encode("admin123"), // Senha: admin123
//                    "900000000",
//                    TipoUsuario.ADMINISTRADOR,
//                    localidade,
//                    "000000000LA000",
//                    LocalDate.of(1990, 1, 1),
//                    TipoDocumento.BILHETE_IDENTIDADE
//            );
//
//            // Admin já nasce verificado/ativo
//            admin.setStatusVerificacao(StatusUsuario.VERIFICADO);
//            admin.setAtivo(true);
//
//            usuarioRepository.save(admin);
//            System.out.println(">>> Administrador criado com sucesso: " + emailAdmin + " / admin123");
//        }
//    }
//}
//}
