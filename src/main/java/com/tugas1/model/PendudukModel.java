package com.tugas1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendudukModel {
	private long id;
	private String nik;
	private String nama;
	private String tempat_lahir;
	private String tanggal_lahir;
	private int jenis_kelamin;
	private int is_wni;
	private long id_keluarga;
	private String agama;
	private String pekerjaan;
	private String status_perkawinan;
	private String status_dalam_keluarga;
	private String golongan_darah;
	private String is_wafat;
	private KeluargaModel keluarga;
}
