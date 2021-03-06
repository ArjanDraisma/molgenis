package org.molgenis.data.annotation.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.data.AttributeMetaData;
import org.molgenis.data.Entity;
import org.molgenis.data.annotation.VariantAnnotator;
import org.molgenis.data.annotation.impl.datastructures.ClinvarData;
import org.molgenis.data.annotation.entity.AnnotatorInfo;
import org.molgenis.data.annotation.entity.AnnotatorInfo.Status;
import org.molgenis.data.annotation.entity.AnnotatorInfo.Type;
import org.molgenis.data.annotation.provider.ClinvarDataProvider;
import org.molgenis.data.annotation.utils.AnnotatorUtils;
import org.molgenis.data.support.DefaultAttributeMetaData;
import org.molgenis.data.vcf.VcfRepository;
import org.molgenis.framework.server.MolgenisSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("clinvarService")
public class ClinVarServiceAnnotator extends VariantAnnotator
{
	private final MolgenisSettings molgenisSettings;

	private static final String NAME = "Clinvar";

	public static final String CLINVAR_FILE_LOCATION_PROPERTY = "clinvar_location";
	private final ClinvarDataProvider clinvarDataProvider;

	public final static String ALLELEID = "AlleleID";
	public final static String TYPE = "Type";
	public final static String GENE_NAME = "Name";
	public final static String GENEID = "GeneID";
	public final static String GENESYMBOL = "GeneSymbol";
	public final static String CLINICALSIGNIFICANCE = "ClinicalSignificance";
	public final static String RS_DBSNP = "RS (dbSNP)";
	public final static String NSV_DBVAR = "nsv (dbVar)";
	public final static String RCVACCESSION = "RCVaccession";
	public final static String TESTEDINGTR = "TestedInGTR";
	public final static String PHENOTYPEIDS = "PhenotypeIDs";
	public final static String ORIGIN = "Origin";
	public final static String ASSEMBLY = "Assembly";
	public final static String CLINVAR_CHROMOSOME = "Chromosome";
	public final static String START = "Start";
	public final static String STOP = "Stop";
	public final static String CYTOGENETIC = "Cytogenetic";
	public final static String REVIEWSTATUS = "ReviewStatus";
	public final static String HGVS_C = "HGVS(c.)";
	public final static String HGVS_P = "HGVS(p.)";
	public final static String NUMBERSUBMITTERS = "NumberSubmitters";
	public final static String LASTEVALUATED = "LastEvaluated";
	public final static String GUIDELINES = "Guidelines";
	public final static String OTHERIDS = "OtherIDs";
	public final static String VARIANTIDS = "VariantID";

	@Autowired
	public ClinVarServiceAnnotator(MolgenisSettings molgenisSettings, ClinvarDataProvider clinvarDataProvider)
			throws IOException
	{
		this.molgenisSettings = molgenisSettings;
		this.clinvarDataProvider = clinvarDataProvider;
	}

	@Override
	public String getSimpleName()
	{
		return NAME;
	}

	@Override
	protected boolean annotationDataExists()
	{
		return new File(molgenisSettings.getProperty(CLINVAR_FILE_LOCATION_PROPERTY)).exists();
	}

	@Override
	public List<Entity> annotateEntity(Entity entity) throws IOException, InterruptedException
	{
		List<Entity> results = new ArrayList<>();

		String chromosome = entity.getString(VcfRepository.CHROM);
		Long position = entity.getLong(VcfRepository.POS);
		String referenceAllele = entity.getString(VcfRepository.REF);
		String alternativeAllele = entity.getString(VcfRepository.ALT);

		List<String> clinvarKeys = Arrays.asList(chromosome, Long.toString(position), referenceAllele,
				alternativeAllele);
		Map<List<String>, ClinvarData> clinvarData = clinvarDataProvider.getClinvarData();
		HashMap<String, Object> resultMap = new HashMap<>();
		if (clinvarData.containsKey(clinvarKeys))
		{
			ClinvarData data = clinvarData.get(clinvarKeys);

			resultMap.put(ALLELEID, data.getAlleleid());
			resultMap.put(TYPE, data.getType());
			resultMap.put(GENE_NAME, data.getGene_name());
			resultMap.put(GENEID, data.getGeneid());
			resultMap.put(GENESYMBOL, data.getGenesymbol());
			resultMap.put(CLINICALSIGNIFICANCE, data.getClinicalsignificance());
			resultMap.put(RS_DBSNP, data.getRs_dbsnp());
			resultMap.put(NSV_DBVAR, data.getNsv_dbvar());
			resultMap.put(RCVACCESSION, data.getRcvaccession());
			resultMap.put(TESTEDINGTR, data.getTestedingtr());
			resultMap.put(PHENOTYPEIDS, data.getPhenotypeids());
			resultMap.put(ORIGIN, data.getOrigin());
			resultMap.put(ASSEMBLY, data.getAssembly());
			resultMap.put(CLINVAR_CHROMOSOME, data.getClinvar_chromosome());
			resultMap.put(START, data.getStart());
			resultMap.put(STOP, data.getStop());
			resultMap.put(CYTOGENETIC, data.getCytogenetic());
			resultMap.put(REVIEWSTATUS, data.getReviewstatus());
			resultMap.put(HGVS_C, data.getHgvs_c());
			resultMap.put(HGVS_P, data.getHgvs_p());
			resultMap.put(NUMBERSUBMITTERS, data.getNumbersubmitters());
			resultMap.put(LASTEVALUATED, data.getLastevaluated());
			resultMap.put(GUIDELINES, data.getGuidelines());
			resultMap.put(OTHERIDS, data.getOtherids());
			resultMap.put(VARIANTIDS, data.getVariantids());

			results.add(AnnotatorUtils.getAnnotatedEntity(this, entity, resultMap));
		}
		else
		{
			results.add(AnnotatorUtils.getAnnotatedEntity(this, entity, resultMap));
		}

		return results;
	}

	@Override
	public List<AttributeMetaData> getOutputMetaData()
	{
		List<AttributeMetaData> metadata = new ArrayList<>();

		metadata.add(new DefaultAttributeMetaData(ALLELEID, FieldTypeEnum.STRING));
		metadata.add(new DefaultAttributeMetaData(TYPE, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(GENE_NAME, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(GENEID, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(GENESYMBOL, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(CLINICALSIGNIFICANCE, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(RS_DBSNP, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(NSV_DBVAR, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(RCVACCESSION, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(TESTEDINGTR, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(PHENOTYPEIDS, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(ORIGIN, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(ASSEMBLY, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(CLINVAR_CHROMOSOME, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(START, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(STOP, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(CYTOGENETIC, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(REVIEWSTATUS, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(HGVS_C, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(HGVS_P, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(NUMBERSUBMITTERS, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(LASTEVALUATED, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(GUIDELINES, FieldTypeEnum.TEXT));
		metadata.add(new DefaultAttributeMetaData(OTHERIDS, FieldTypeEnum.TEXT));

		return metadata;
	}

	@Override
	public AnnotatorInfo getInfo()
	{
		return AnnotatorInfo.create(Status.BETA, Type.PHENOTYPE_ASSOCIATION, "clinvar", "", getOutputMetaData());
	}

}
