package me.flourick.fvt.utils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;

import me.flourick.fvt.FVT;

/**
 * Downloads this mods translations from Crowdin and loads them on startup.
 * 
 * @author Flourick
 */
public class CrowdinTranslations
{
	public static void download()
	{
		new CrowdinRunner().start();
	}

	private static class CrowdinRunner extends Thread
	{
		private final Map<String, String> langs;
		private final Path translationsDir = Paths.get(FVT.MC.runDirectory.getAbsolutePath(), "config", "fvt", "translations");

		CrowdinRunner()
		{
			// all 119 beautiful languages, custom languages are commented for now
			langs = new HashMap<>(){{
				put("af", "af_za");
				put("ar", "ar_sa");
				put("ast", "ast_es");
				put("az", "az_az");
				put("ba", "ba_ru");
				//put("bar", "bar");			// Bavaria
				put("be", "be_by");
				put("bg", "bg_bg");
				put("br-FR", "br_fr");
				//put("brb", "brb");			// Brabantian
				put("bs", "bs_ba");
				put("ca", "ca_es");
				put("cs", "cs_cz");
				put("cy", "cy_gb");
				put("da", "da_dk");
				put("de-AT", "de_at");
				put("de-CH", "de_ch");
				put("de", "de_de");
				put("el", "el_gr");
				put("de-AT", "en_au");
				put("en-CA", "en_ca");
				put("en-GB", "en_gb");
				put("en-NZ", "en_nz");
				put("en-PT", "en_pt");
				put("en-UD", "en_ud");
				put("en-US", "en_us");
				//put("enp", "enp");			// Anglish
				//put("enws", "enws");			// Shakespearean English
				put("eo", "eo_uy");
				put("es-AR", "es_ar");
				put("es-CL", "es_cl");
				put("es-EC", "es_ec");
				put("es-ES", "es_es");
				put("es-MX", "es_mx");
				put("es-UY", "es_uy");
				put("es-VE", "es_ve");
				//put("esan", "esan");			// Andalusian
				put("et", "et_ee");
				put("eu", "eu_es");
				put("fa", "fa_ir");
				put("fi", "fi_fi");
				put("fil", "fil_ph");
				put("fo", "fo_fo");
				put("fr-CA", "fr_ca");
				put("fr", "fr_fr");
				put("fra-DE", "fra_de");
				put("fy-NL", "fy_nl");
				put("ga-IE", "ga_ie");
				put("gd", "gd_gb");
				put("gl", "gl_es");
				put("haw", "haw_us");
				put("he", "he_il");
				put("hi", "hi_in");
				put("hr", "hr_hr");
				put("hu", "hu_hu");
				put("hy-AM", "hy_am");
				put("id", "id_id");
				put("ig", "ig_ng");
				put("ido", "io_en");
				put("is", "is_is");
				//put("isv", "isv");			// Interslavic
				put("it", "it_it");
				put("ja", "ja_jp");
				put("jbo", "jbo_en");
				put("ka", "ka_ge");
				put("kk", "kk_kz");
				put("kn", "kn_in");
				put("ko", "ko_kr");
				//put("ksh", "ksh");			// Ripuarian
				put("kw", "kw_gb");
				put("la-LA", "la_la");
				put("lb", "lb_lu");
				put("li", "li_li");
				put("lol", "lol_us");
				put("lt", "lt_lt");
				put("lv", "lv_lv");
				//put("lzh", "lzh");			// Classical Chinese
				put("mk", "mk_mk");
				put("mn", "mn_mn");
				put("ms", "ms_my");
				put("mt", "mt_mt");
				put("nds", "nds_de");
				put("nl-BE", "nl_be");
				put("nl", "nl_nl");
				put("nn-NO", "nn_no");
				put("no", "no_no‌");
				put("oc", "oc_fr");
				//put("ovd", "ovd");			// Elfdalian
				put("pl", "pl_pl");
				put("pt-BR", "pt_br");
				put("pt-PT", "pt_pt");
				put("qya-AA", "qya_aa");
				put("ro", "ro_ro");
				//put("rpr", "rpr");			// Russian (pre-revolutionary)
				put("ru", "ru_ru");
				put("se", "se_no");
				put("sk", "sk_sk");
				put("sl", "sl_si");
				put("so", "so_so");
				put("sq", "sq_al");
				put("sr", "sr_sp");
				put("sv-SE", "sv_se");
				//put("sxu", "sxu");			// Upper Saxon German
				//put("szl", "szl");			// Silesian
				put("ta", "ta_in");
				put("th", "th_th");
				put("tl", "tl_ph");
				put("tlh-AA", "tlh_aa");
				put("tr", "tr_tr");
				put("tt-RU", "tt_ru");
				put("uk", "uk_ua");
				put("val-ES", "val_es");
				put("vec", "vec_it");
				put("vi", "vi_vn");
				put("yi", "yi_de");
				put("yo", "yo_ng");
				put("zh-CN", "zh_cn");
				put("zh-HK", "zh_hk");
				put("zh-TW", "zh_tw");
			}};
		}

		@Override
		public void run()
		{
			boolean download = true;

			if(!Files.exists(translationsDir)) {
				try {
					Files.createDirectories(translationsDir);
				}
				catch (IOException e) {
					LogManager.getLogger().error("[FVT] Cannot create required translation directories:", e.toString());
				}
			}
			else {
				// a bit simple but works
				try {
					Instant lastDownload = Files.readAttributes(translationsDir, BasicFileAttributes.class).lastModifiedTime().toInstant().plus(24, ChronoUnit.HOURS);
					
					if(lastDownload.isAfter(Instant.now())) {
						download = false;
					}
				}
				catch (IOException e) {
					// nada, let download be true since we have no idea when was the last download
				}
			}

			if(download) {
				LogManager.getLogger().info("[FVT] Downloading translations...");

				try(ZipInputStream zipInputStream = new ZipInputStream(new URL("https://crowdin.com/backend/download/project/flours-various-tweaks.zip").openStream())) {
					ZipEntry entry;
					Pattern pattern = Pattern.compile("[a-zA-Z-]{2,6}/en_us\\.json");

					while((entry = zipInputStream.getNextEntry()) != null) {
						if(pattern.matcher(entry.getName()).matches()) {
							String filename = langs.get(entry.getName().split("/")[0]) + ".json";
							Files.copy(zipInputStream, translationsDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
						}
					}

					LogManager.getLogger().info("[FVT] Translations successfully downloaded!");
				}
				catch(IOException e) {
					LogManager.getLogger().error("[FVT] Cannot download translations:", e.toString());
				}
			}

			// TODO: load translation jsons in, how? no fokin idea
		}
	}
}
