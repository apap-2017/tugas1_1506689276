package com.tugas1.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tugas1.dao.PendudukMapper;
import com.tugas1.model.KecamatanModel;
import com.tugas1.model.KelurahanModel;
import com.tugas1.model.KotaModel;
import com.tugas1.model.PendudukModel;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class PendudukServiceDatabase implements PendudukService {
	
	@Autowired
	private PendudukMapper pendudukMapper;

	@Override
	public PendudukModel getPenduduk(String nik)  {
		log.info("selecting penduduk with nik " + nik);
		return pendudukMapper.selectPenduduk(nik);
	}
	
	@Override
	public List<PendudukModel> getPendudukByPrefixNik(String prefix)  {
		log.info("selecting penduduk with prefix nik " + prefix);
		return pendudukMapper.getPendudukByPrefixNik(prefix);
	}
	
	@Override
	public boolean insertPenduduk(PendudukModel pendudukModel) {
		log.info("inserting penduduk with nik " + pendudukModel.getNik());
		try {
			long newId = pendudukMapper.getLatestPenduduk().getId() + 1;
			pendudukModel.setId(newId);
			pendudukMapper.insertPenduduk(pendudukModel);
			
			return true;
		} catch (Exception e) {
			log.error("error on inserting penduduk with nik " + pendudukModel.getNik(), e);
			return false;
		}
	}
	
	public boolean updatePenduduk(String oldNik, PendudukModel penduduk) {
		log.info("updating penduduk with nik " + penduduk.getNik());
		try {
			long newId = pendudukMapper.getLatestPenduduk().getId() + 1;
			penduduk.setId(newId);
			pendudukMapper.updatePenduduk(penduduk, oldNik);
			
			return true;
		} catch (Exception e) {
			log.error("error on updating penduduk with nik " + oldNik, e);
			return false;
		}
	}
	
	public List<KotaModel> selectAllKota(){
		return pendudukMapper.selectAllKota();
	}
	
	public List<KecamatanModel> selectKecamatan(Long id_kota) {
		return pendudukMapper.selectKecamatan(id_kota);
	}
	
	public List<KelurahanModel> selectKelurahan(Long id_kecamatan) {
		return pendudukMapper.selectKelurahan(id_kecamatan);
	}
	
	public List<PendudukModel> selectPendudukByKelurahan(Long id_kelurahan) {
		return pendudukMapper.selectPendudukByKelurahan(id_kelurahan);
	}
	
	public KelurahanModel getKelurahanById(Long id_kelurahan) {
		return pendudukMapper.selectKelurahanById(id_kelurahan);
	}
}
