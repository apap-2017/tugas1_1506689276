package com.tugas1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KecamatanModel {
	private long id;
	private long id_kota;
	private String kode_kecamatan;
	private String nama_kecamatan;
	private KotaModel kota;
}
