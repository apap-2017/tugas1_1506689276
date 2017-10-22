package com.tugas1.service;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.tugas1.model.KecamatanModel;
import com.tugas1.model.KelurahanModel;
import com.tugas1.model.KotaModel;
import com.tugas1.model.PendudukModel;

public interface PendudukService {
	public PendudukModel getPenduduk(String nik);
	
	public boolean insertPenduduk(PendudukModel pendudukModel);
	
	public List<PendudukModel> getPendudukByPrefixNik(String prefix);

	public boolean updatePenduduk(String oldNik, PendudukModel penduduk);
	
	public List<KotaModel> selectAllKota();
	
	public List<KecamatanModel> selectKecamatan(Long id_kota);
	
	public List<KelurahanModel> selectKelurahan(Long id_kecamatan);
	
	public List<PendudukModel> selectPendudukByKelurahan(Long id_kelurahan);
	
	public KelurahanModel getKelurahanById(Long id_kelurahan);
}
