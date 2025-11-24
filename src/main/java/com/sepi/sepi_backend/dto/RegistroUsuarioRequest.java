package com.sepi.sepi_backend.dto;

import com.sepi.sepi_backend.enums.TipoDocumento;
import com.sepi.sepi_backend.enums.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RegistroUsuarioRequest
{

    @NotBlank(message = "O nome completo é obrigatório.")
    private String nomeCompleto;

    @Email(message = "Email deve ser válido.")
    @NotBlank(message = "O email é obrigatório.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String palavraPasse;

    @NotBlank(message = "O telefone é obrigatório.")
    private String telefone;

    @NotNull(message = "O tipo de usuário é obrigatório.")
    private TipoUsuario tipoUsuario;

    @NotBlank(message = "O código da localidade é obrigatório.")
    private String pkLocalidade;

    @NotNull(message = "O tipo de documento de identificação é obrigatório.")
    private TipoDocumento tipoIdentificacao; // Certifique-se que o nome bate com o Enum usado na Entidade

    @NotBlank(message = "O número do documento é obrigatório.")
    private String numeroDocumento;

    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve ser uma data passada.")
    private LocalDate dataNascimento;
}
