/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.util;

import com.sepi.sepi_backend.entity.Localidade;
import com.sepi.sepi_backend.repository.LocalidadeRepository;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 *
 * @author azm
 */
@Component
@RequiredArgsConstructor
public class LocalidadeInitializer implements CommandLineRunner
{

    private final LocalidadeRepository localidadeRepository;

    @Override
    public void run (String... args) throws Exception
    {
        if (localidadeRepository.count() == 0)
        {
            // 1. Criar País/Província (Ex: Luanda como Província com PK 'LA')
            Localidade luanda = Localidade.builder()
                    .pkLocalidade("LA")
                    .designacao("Luanda (Província)")
                    .build();

            localidadeRepository.save(luanda);

            // 2. Criar Municípios (Filhos de Luanda)
            Localidade viana = Localidade.builder()
                    .pkLocalidade("LA_V")
                    .designacao("Viana")
                    .localidadePai(luanda)
                    .build();

            Localidade belas = Localidade.builder()
                    .pkLocalidade("LA_B")
                    .designacao("Belas")
                    .localidadePai(luanda)
                    .build();

            localidadeRepository.saveAll(Arrays.asList(viana, belas));

            System.out.println(">>> Base de dados de Localidades inicializada com sucesso: LA, LA_V, LA_B");
        }
    }
}
