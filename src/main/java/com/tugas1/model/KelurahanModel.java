package com.tugas1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KelurahanModel {
	private long id;
	private long id_kecamatan;
	private String kode_kelurahan;
	private String nama_kelurahan;
	private String kode_pos;
	private KecamatanModel kecamatan;
}
