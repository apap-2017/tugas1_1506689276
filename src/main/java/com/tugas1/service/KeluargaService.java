package com.tugas1.service;

import java.util.List;

import com.tugas1.model.KeluargaModel;
import com.tugas1.model.KelurahanModel;
import com.tugas1.model.PendudukModel;

public interface KeluargaService {
	public KeluargaModel getKeluarga(String nkk);
	
	public KeluargaModel getKeluargaById(long id);
	
	public boolean insertKeluarga(KeluargaModel keluargaModel);
	
	public KelurahanModel getKelurahanById(long id);
	
	public List<KeluargaModel> getKeluargaByPrefixNkk(String prefix);
	
	public List<KelurahanModel> getAllKelurahan();
	
	public boolean updateKeluarga(KeluargaModel keluargaModel, String oldNkk);
	
	public boolean updateKeluarga2(KeluargaModel keluargaModel, String oldNkk);
}
