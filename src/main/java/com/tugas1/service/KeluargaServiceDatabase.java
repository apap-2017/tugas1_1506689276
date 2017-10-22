package com.tugas1.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tugas1.dao.KeluargaMapper;
import com.tugas1.dao.PendudukMapper;
import com.tugas1.model.KeluargaModel;
import com.tugas1.model.KelurahanModel;
import com.tugas1.model.PendudukModel;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class KeluargaServiceDatabase implements KeluargaService {
	
	@Autowired
	private PendudukMapper pendudukMapper;
	
	@Autowired
	private KeluargaMapper keluargaMapper;

	@Override
	public KeluargaModel getKeluarga(String nkk)  {
		log.info("selecting keluarga with nkk " + nkk);
		KeluargaModel keluargaModel = keluargaMapper.selectKeluarga(nkk);
		if (keluargaModel != null ) {
			List<PendudukModel> anggota_keluarga = pendudukMapper.selectPendudukByIdKeluarga(keluargaModel.getId());
			keluargaModel.setAnggota_keluarga(anggota_keluarga);
		}
		
		return keluargaModel;
	}
	
	public KeluargaModel getKeluargaById(long id) {
		KeluargaModel keluargaModel = pendudukMapper.selectKeluargaById(id);
		if (keluargaModel != null ) {
			List<PendudukModel> anggota_keluarga = pendudukMapper.selectPendudukByIdKeluarga(keluargaModel.getId());
			keluargaModel.setAnggota_keluarga(anggota_keluarga);
		}
		
		return keluargaModel;
	}
	
	@Override
	public List<KeluargaModel> getKeluargaByPrefixNkk(String prefix)  {
		log.info("selecting keluarga with prefix nkk " + prefix);
		return keluargaMapper.getKeluargaByPrefixNkk(prefix);
	}
	
	public KelurahanModel getKelurahanById(long id) {
		return keluargaMapper.selectKelurahanById(id);
	}
	
	@Override
	public List<KelurahanModel> getAllKelurahan()  {
		return keluargaMapper.getAllKelurahan();
	}
	
	@Override
	public boolean insertKeluarga(KeluargaModel keluargaModel) {
		log.info("inserting keluarga with nkk " + keluargaModel.getNomor_kk());
		try {
			long newId = keluargaMapper.getLatestKeluarga().getId() + 1;
			keluargaModel.setId(newId);
			keluargaMapper.insertKeluarga(keluargaModel);
			
			return true;
		} catch (Exception e) {
			log.error("error on inserting keluarga with nkk " + keluargaModel.getNomor_kk(), e);
			return false;
		}
	}
	
	@Override
	public boolean updateKeluarga(KeluargaModel keluargaModel, String oldNkk) {
		log.info("updating keluarga with nkk " + keluargaModel.getNomor_kk());
		try {
			long newId = keluargaMapper.getLatestKeluarga().getId() + 1;
			keluargaModel.setId(newId);
			keluargaMapper.updateKeluarga(keluargaModel, oldNkk);
			
			return true;
		} catch (Exception e) {
			log.error("error on updating keluarga with nkk " + keluargaModel.getNomor_kk(), e);
			return false;
		}
	}
	
	@Override
	public boolean updateKeluarga2(KeluargaModel keluargaModel, String oldNkk) {
		log.info("updating keluarga with nkk " + keluargaModel.getNomor_kk());
		try {
			keluargaMapper.updateKeluarga2(keluargaModel, oldNkk);
			
			return true;
		} catch (Exception e) {
			log.error("error on updating keluarga with nkk " + keluargaModel.getNomor_kk(), e);
			return false;
		}
	}
	
	
	
	

}
