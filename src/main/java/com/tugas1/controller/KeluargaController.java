package com.tugas1.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tugas1.model.KeluargaModel;
import com.tugas1.model.KelurahanModel;
import com.tugas1.model.PendudukModel;
import com.tugas1.service.KeluargaService;
import com.tugas1.service.PendudukService;

@Controller
public class KeluargaController {
	
	@Autowired
	PendudukService pendudukService;
	
	@Autowired
	KeluargaService keluargaService;

	@RequestMapping("/keluarga")
	public String getKeluarga(@RequestParam(value = "nkk", required=false) String nkk, Model model) {
		if (nkk == null || nkk == "") {
			return "getKeluarga";
		}
		
		KeluargaModel keluarga = keluargaService.getKeluarga(nkk);
		if (keluarga == null) {
			model.addAttribute("message", "Tidak ada data keluarga dengan NKK " + nkk);
			return "notfound";
		}
		
		model.addAttribute("keluarga", keluarga);
		return "keluarga";	
	}
	
	@RequestMapping("/keluarga/tambah")
	public String addKeluarga(Model model) {
		List<KelurahanModel> kelurahanModel = keluargaService.getAllKelurahan();
		
		model.addAttribute("keluarga", new KeluargaModel());
		
		KeluargaModel oldKeluarga = new KeluargaModel();
		oldKeluarga.setKelurahan(new KelurahanModel());
		model.addAttribute("oldkeluarga", oldKeluarga);
		model.addAttribute("allkelurahan", kelurahanModel);
		return "tambahKeluarga";		
	}
	
	@RequestMapping(value = "/keluarga/tambah", method = RequestMethod.POST)
	public String addPendudukSubmit(@ModelAttribute KeluargaModel keluarga, Model model) {
		// TODO
		KelurahanModel kelurahanModel = keluargaService.getKelurahanById(keluarga.getId_kelurahan());
		
		if (kelurahanModel == null) {
			model.addAttribute("errorMessage", "Tidak dapat menemukan kelurahan dengan kode " + keluarga.getId_kelurahan());
			return "errorForm1";
		}
		
		String kodeNonId = "";
		
		String kodeByLokasi = kelurahanModel.getKode_kelurahan().substring(0, 6); // sudah include semuanya
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
		String tgl = sdf.format(new Date());
		
		kodeNonId = kodeByLokasi + tgl;
		
		List<KeluargaModel> keluargaWithSameKodePrefix = keluargaService.getKeluargaByPrefixNkk(kodeNonId + "%");
		int size = keluargaWithSameKodePrefix.size();
		String incrementalId = String.format("%04d", size + 1);
		
		
		String nkk = kodeNonId + incrementalId;
		keluarga.setNomor_kk(nkk);
		if (keluargaService.insertKeluarga(keluarga)) {
			model.addAttribute("message", "Sukses menambah data keluarga baru NKK " + nkk);
			return "success";
		} else {
			model.addAttribute("errorMessage", "There was an error when trying to insert a new keluarga NKK " + nkk);
			return "errorForm1";
		}	
	}
	
	@RequestMapping("/keluarga/ubah/{nkk}")
	public String updateKeluarga(@PathVariable String nkk, Model model) {
		KeluargaModel keluargaModel = keluargaService.getKeluarga(nkk);
		List<KelurahanModel> kelurahanModel = keluargaService.getAllKelurahan();
		
		model.addAttribute("keluarga", new KeluargaModel());
		model.addAttribute("allkelurahan", kelurahanModel);
		model.addAttribute("oldkeluarga", keluargaModel);
		return "tambahKeluarga";		
	}
	
	@RequestMapping(value="/keluarga/ubah/{nkk}", method = RequestMethod.POST)
	public String updateKeluargaSubmit(@PathVariable String nkk, @ModelAttribute KeluargaModel keluarga, Model model) {
		// TODO
		KeluargaModel keluargaSekarang = keluargaService.getKeluarga(nkk);
		KelurahanModel kelurahanModel = keluargaService.getKelurahanById(keluarga.getId_kelurahan());
		
		if (kelurahanModel == null) {
			model.addAttribute("errorMessage", "Tidak dapat menemukan kelurahan dengan kode " + keluarga.getId_kelurahan());
			return "errorForm1";
		}
		
		String kodeNonId = "";
		
		String kodeByLokasi = kelurahanModel.getKode_kelurahan().substring(0, 6); // sudah include semuanya
		String tgl = keluargaSekarang.getNomor_kk().substring(6,12);
		
		kodeNonId = kodeByLokasi + tgl;
		
		List<KeluargaModel> keluargaWithSameKodePrefix = keluargaService.getKeluargaByPrefixNkk(kodeNonId + "%");
		int size = keluargaWithSameKodePrefix.size();
		
		String oldKodeNonId = keluargaSekarang.getNomor_kk().substring(0, 12);
		
		String newNkk;
		if (!oldKodeNonId.equals(kodeNonId)) {
			String incrementalId = String.format("%04d", size + 1);
			
			newNkk = kodeNonId + incrementalId;
		} else {
			newNkk = keluargaSekarang.getNomor_kk();
		}
		
		keluarga.setNomor_kk(newNkk);
		if (keluargaService.updateKeluarga(keluarga, nkk)) {
			//change nik
			keluargaSekarang = keluargaService.getKeluarga(newNkk);
			String kodeByLokasiBaru = keluargaSekarang.getKelurahan().getKecamatan().getKode_kecamatan(); // sudah include kode provinsi dan kota
			kodeByLokasiBaru = kodeByLokasiBaru.substring(0, 6);
			
			List<PendudukModel> anggota = keluargaSekarang.getAnggota_keluarga();
			for (PendudukModel penduduk : anggota) {
				String nik = penduduk.getNik();
				String kodeByLokasiNik = nik.substring(0, 6);
				
				if (kodeByLokasiBaru == kodeByLokasiNik) {
					continue;
				}
				
				String nikNonLokasi = nik.substring(6);
				String newNik = kodeByLokasiBaru + nikNonLokasi;
				penduduk.setNik(newNik);
				
				pendudukService.updatePenduduk(nik, penduduk);
			}
			
			model.addAttribute("message", "Sukses update data keluarga NKK " + nkk);
			return "success";
		} else {
			model.addAttribute("errorMessage", "There was an error when trying to update a new keluarga NKK " + nkk);
			return "errorForm1";
		}	
	}
}
