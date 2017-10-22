package com.tugas1.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.tugas1.model.KecamatanModel;
import com.tugas1.model.KeluargaModel;
import com.tugas1.model.KelurahanModel;
import com.tugas1.model.KotaModel;
import com.tugas1.model.PendudukModel;

@Mapper
public interface KeluargaMapper {
	@Select("select * from keluarga where nomor_kk=#{nkk}")
	@Results(value = { 
			@Result(property = "kelurahan", column = "id_kelurahan", 
					javaType = KelurahanModel.class, 
					many = @Many(select = "selectKelurahanById")) 
	})
	KeluargaModel selectKeluarga(@Param("nkk") String nkk);
	
	@Select("select * from kelurahan")
	@Results(value = { 
			@Result(property = "kecamatan", column = "id_kecamatan", 
					javaType = KecamatanModel.class, 
					many = @Many(select = "selectKecamatanById")) 
	})
	List<KelurahanModel> getAllKelurahan();
	
	@Select("select * from kelurahan where id=#{id}")
	@Results(value = { 
			@Result(property = "kecamatan", column = "id_kecamatan", 
					javaType = KecamatanModel.class, 
					many = @Many(select = "selectKecamatanById")) 
	})
	KelurahanModel selectKelurahanById(@Param("id") Long id);
	
	@Select("select * from kecamatan where id=#{id}")
	@Results(value = { 
			@Result(property = "kota", column = "id_kota", 
					javaType = KotaModel.class, 
					many = @Many(select = "selectKotaById")) 
	})
	KecamatanModel selectKecamatanById(@Param("id") Long id);
	
	@Select("select * from kota where id=#{id}")
	KotaModel selectKotaById(@Param("id") Long id);
	
	@Select("select * from keluarga order by id DESC LIMIT 1")
	KeluargaModel getLatestKeluarga();
	
	@Select("select * from keluarga where nomor_kk LIKE #{prefix}")
	List<KeluargaModel> getKeluargaByPrefixNkk(String prefix);
	
	@Insert("INSERT INTO keluarga (id, nomor_kk, alamat, RT, RW, id_kelurahan) VALUES (#{id}, #{nomor_kk}, #{alamat}, #{RT}, #{RW}, #{id_kelurahan})")
	void insertKeluarga(KeluargaModel keluarga);
	
	@Update("UPDATE keluarga SET nomor_kk=#{keluarga.nomor_kk}, alamat=#{keluarga.alamat}, RT=#{keluarga.RT}, RW=#{keluarga.RW}, id_kelurahan=#{keluarga.id_kelurahan} WHERE nomor_kk=#{oldNkk}")
	void updateKeluarga(@Param("keluarga") KeluargaModel keluarga, @Param("oldNkk") String oldNkk);
	
	@Update("UPDATE keluarga SET is_tidak_berlaku=#{keluarga.is_tidak_berlaku} WHERE nomor_kk=#{oldNkk}")
	void updateKeluarga2(@Param("keluarga") KeluargaModel keluarga, @Param("oldNkk") String oldNkk);
}
