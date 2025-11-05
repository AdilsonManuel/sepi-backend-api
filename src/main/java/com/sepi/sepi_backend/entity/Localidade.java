package com.sepi.sepi_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "localidade")
@Data // Inclui Getters, Setters, toString, equals e hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Localidade implements Serializable
{
	
	@Id
	@Column(name = "pk_localidade")
	private String pkLocalidade;
	
	@Column(name = "designacao", nullable = false, unique = true)
	private String designacao;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_localidade_pai", referencedColumnName = "pk_localidade")
	@JsonBackReference // Evita recursão infinita na serialização JSON do pai
	private Localidade localidadePai;
	
	@OneToMany(mappedBy = "localidadePai", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference // Permite a serialização da lista de filhos
	private List<Localidade> localidadesFilhas;
}
