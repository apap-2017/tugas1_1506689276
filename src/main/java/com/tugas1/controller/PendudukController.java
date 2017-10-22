package com.tugas1.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tugas1.dao.PendudukMapper;
import com.tugas1.model.KecamatanModel;
import com.tugas1.model.KeluargaModel;
import com.tugas1.model.KelurahanModel;
import com.tugas1.model.KotaModel;
import com.tugas1.model.PendudukModel;
import com.tugas1.service.KeluargaService;
import com.tugas1.service.PendudukService;
import com.tugas1.service.PendudukServiceDatabase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class PendudukController {
	@Autowired
	PendudukService pendudukService;
	
	@Autowired
	KeluargaService keluargaService;


	@RequestMapping("/penduduk")
	public String getPenduduk(@RequestParam(value = "nik", required=false) String nik, Model model) {
		if (nik == null || nik == "") {
			model.addAttribute("errorMessage", "NIK Tidak boleh kosong, harap perbaiki.");
			return "errorForm1";
		}
		
		PendudukModel penduduk = pendudukService.getPenduduk(nik);
		if (penduduk == null) {
			model.addAttribute("message", "Tidak ada data penduduk dengan NIK " + nik);
			return "notfound";
		}
		
		model.addAttribute("penduduk", penduduk);
		return "penduduk";	
	}
	
	@RequestMapping("/penduduk/ubah/{nik}")
	public String updatePenduduk(@PathVariable String nik, Model model) {
		PendudukModel pendudukModel = pendudukService.getPenduduk(nik);
		
		model.addAttribute("penduduk", new PendudukModel());
		model.addAttribute("oldpenduduk", pendudukModel);
		return "tambahPenduduk";		
	}
	
	@RequestMapping("/penduduk/tambah")
	public String addPenduduk(Model model) {
		model.addAttribute("penduduk", new PendudukModel());
		
		PendudukModel oldpenduduk = new PendudukModel();
		oldpenduduk.setKeluarga(new KeluargaModel());
		model.addAttribute("oldpenduduk", oldpenduduk);
		return "tambahPenduduk";		
	}
	
	@RequestMapping(value="/penduduk/mati", method = RequestMethod.POST)
	public String updatePendudukSubmit(@RequestParam(value="nik") String nik, Model model) {
		PendudukModel pendudukSekarang = pendudukService.getPenduduk(nik);
		if (pendudukSekarang == null) {
			model.addAttribute("message", "Tidak ada data penduduk dengan NIK " + nik);
			return "notfound";
		}
		
		if (pendudukSekarang.getIs_wafat().equals("0")) 
			pendudukSekarang.setIs_wafat("1");
		else
			pendudukSekarang.setIs_wafat("0");
		
		pendudukSekarang.setId_keluarga(pendudukSekarang.getKeluarga().getId());
		
		if (pendudukService.updatePenduduk(nik, pendudukSekarang)) {
			
			KeluargaModel keluargaModel = keluargaService.getKeluargaById(pendudukSekarang.getId_keluarga());
			List<PendudukModel> anggota = keluargaModel.getAnggota_keluarga();
			int tidakberlaku = 1;
			for (PendudukModel penduduk : anggota) {
				if (penduduk.getIs_wafat().equals("0")) {
					tidakberlaku = 0;
					break;
				}
			}
			
			log.error(tidakberlaku + "da");
			
			keluargaModel.setIs_tidak_berlaku(tidakberlaku);
			keluargaService.updateKeluarga2(keluargaModel, keluargaModel.getNomor_kk());
			
			model.addAttribute("message", "Sukses update data kematian penduduk NIK " + nik);
			return "success";
		} else {
			model.addAttribute("errorMessage", "There was an error when trying to insert a new penduduk NIK " + nik);
			return "errorForm1";
		}
		
	}
	
	@RequestMapping(value="/penduduk/ubah/{nik}", method = RequestMethod.POST)
	public String updatePendudukSubmit(@PathVariable String nik, @ModelAttribute PendudukModel penduduk, Model model) {
		// TODO
		PendudukModel pendudukSekarang = pendudukService.getPenduduk(nik);
		KeluargaModel keluargaModel = keluargaService.getKeluargaById(penduduk.getId_keluarga());
		
		if (keluargaModel == null) {
			model.addAttribute("errorMessage", "Tidak dapat menemukan keluarga dengan kode " + penduduk.getId_keluarga());
			return "errorForm1";
		}
	
		String kodeByLokasi = keluargaModel.getKelurahan().getKecamatan().getKode_kecamatan(); // sudah include kode provinsi dan kota
		kodeByLokasi = kodeByLokasi.substring(0, 6); // because every kecamatan always ended with 0
		
		log.error(kodeByLokasi);
		
		String[] splitTglLahir = penduduk.getTanggal_lahir().split("-");
		String tgl = Integer.parseInt(splitTglLahir[2]) + (penduduk.getJenis_kelamin() == 1 ? 40 : 0) + ""; // + 40 if woman
		String bln = splitTglLahir[1];
		String thn = splitTglLahir[0].substring(2, 4); // last 2 digits of year
		String kodeByLahir = tgl + bln + thn;
		
		String kodeNonId = kodeByLokasi + kodeByLahir;
		
		List<PendudukModel> pendudukWithSameKodePrefix = pendudukService.getPendudukByPrefixNik(kodeNonId + "%");
		int size = pendudukWithSameKodePrefix.size();
		
		String oldKodeNonId = pendudukSekarang.getNik().substring(0, 12);
		
		String newNik;
		if (!oldKodeNonId.equals(kodeNonId)) {
			String incrementalId = String.format("%04d", size + 1);
			
			newNik = kodeNonId + incrementalId;
		} else {
			newNik = pendudukSekarang.getNik();
		}
		
		penduduk.setNik(newNik);
		if (pendudukService.updatePenduduk(nik, penduduk)) {
			model.addAttribute("message", "Sukses update data penduduk NIK " + nik);
			return "success";
		} else {
			model.addAttribute("errorMessage", "There was an error when trying to insert a new penduduk NIK " + nik);
			return "errorForm1";
		}	
	}
	
	@RequestMapping(value = "/penduduk/tambah", method = RequestMethod.POST)
	public String addPendudukSubmit(@ModelAttribute PendudukModel penduduk, Model model) {
		// TODO
		KeluargaModel keluargaModel = keluargaService.getKeluargaById(penduduk.getId_keluarga());
		
		if (keluargaModel == null) {
			model.addAttribute("errorMessage", "Tidak dapat menemukan keluarga dengan kode " + penduduk.getId_keluarga());
			return "errorForm1";
		}
	
		String kodeByLokasi = keluargaModel.getKelurahan().getKecamatan().getKode_kecamatan(); // sudah include kode provinsi dan kota
		kodeByLokasi = kodeByLokasi.substring(0, 6); // because every kecamatan always ended with 0
		
		String[] splitTglLahir = penduduk.getTanggal_lahir().split("-");
		String tgl = Integer.parseInt(splitTglLahir[2]) + (penduduk.getJenis_kelamin() == 1 ? 40 : 0) + ""; // + 40 if woman
		String bln = splitTglLahir[1];
		String thn = splitTglLahir[0].substring(2, 4); // last 2 digits of year
		String kodeByLahir = tgl + bln + thn;
		
		String kodeNonId = kodeByLokasi + kodeByLahir;
		
		List<PendudukModel> pendudukWithSameKodePrefix = pendudukService.getPendudukByPrefixNik(kodeNonId + "%");
		int size = pendudukWithSameKodePrefix.size();
		String incrementalId = String.format("%04d", size + 1);
		
		
		String nik = kodeNonId + incrementalId;
		penduduk.setNik(nik);
		if (pendudukService.insertPenduduk(penduduk)) {
			model.addAttribute("message", "Sukses menambah data penduduk baru NIK " + nik);
			return "success";
		} else {
			model.addAttribute("errorMessage", "There was an error when trying to insert a new penduduk NIK " + nik);
			return "errorForm1";
		}	
	}
	
	
	@RequestMapping("/penduduk/cari")
	public String cariPenduduk(@RequestParam(value = "kt", required=false) String kt, @RequestParam(value = "kc", required=false) String kc, @RequestParam(value = "kl", required=false) String kl, Model model) throws ParseException {
		// if tiga tiganya null, berarti harus dikasih halaman pencarian
		List<KotaModel> listKota = pendudukService.selectAllKota();
		List<KecamatanModel> listKecamatan = null;
		List<KelurahanModel> listKelurahan = null;
		
		if (kt != null) {
			long id_kota = Long.parseLong(kt);
			listKecamatan = pendudukService.selectKecamatan(id_kota);
			model.addAttribute("kt", id_kota);
		}
		
		if (kc != null) {
			long id_kecamatan = Long.parseLong(kc);
			listKelurahan = pendudukService.selectKelurahan(id_kecamatan);
			model.addAttribute("kc", id_kecamatan);
		}
		
		model.addAttribute("listKota", listKota);
		model.addAttribute("listKecamatan", listKecamatan);
		model.addAttribute("listKelurahan", listKelurahan);
		
		if (kt != null && kc != null && kl != null) {
			List<PendudukModel> listPenduduk = pendudukService.selectPendudukByKelurahan(Long.parseLong(kl));
			model.addAttribute("listPenduduk", listPenduduk);
			
			PendudukModel oldest = null;
			PendudukModel youngest = null;
			
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			
			for (PendudukModel penduduk : listPenduduk) {
				if (oldest == null && youngest == null) {
					oldest = penduduk;
					youngest = penduduk;
					continue;
				}
				
				Date o = format.parse(oldest.getTanggal_lahir());
				Date c = format.parse(penduduk.getTanggal_lahir());
				if (c.compareTo(o) < 0) {
					oldest = penduduk;
				}
				
				Date y = format.parse(youngest.getTanggal_lahir());
				if (c.compareTo(y) > 0) {
					youngest = penduduk;
				}
			}
			
			KelurahanModel kelurahanModel = pendudukService.getKelurahanById(Long.parseLong(kl));
			model.addAttribute("kelurahan", kelurahanModel);
			model.addAttribute("oldest", oldest);
			model.addAttribute("youngest", youngest);
			
			return "hasilpencarian";
		}
		
		return "pencarian";
	}
	
	
	
}
