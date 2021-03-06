package org.molgenis.security.freemarker;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.molgenis.security.core.MolgenisPermissionService;
import org.molgenis.security.core.Permission;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class HasPermissionDirectiveTest
{
	private HasPermissionDirective directive;
	private MolgenisPermissionService molgenisPermissionService;
	private StringWriter envWriter;
	private Template fakeTemplate;

	@BeforeMethod
	public void setUp()
	{
		molgenisPermissionService = mock(MolgenisPermissionService.class);
		directive = new HasPermissionDirective(molgenisPermissionService);
		envWriter = new StringWriter();
		fakeTemplate = Template
				.getPlainTextTemplate("name", "content", new Configuration(Configuration.VERSION_2_3_21));
	}

	@Test
	public void executeWithPermission() throws TemplateException, IOException
	{
		when(molgenisPermissionService.hasPermissionOnEntity("entity", Permission.COUNT)).thenReturn(true);

		Map<String, Object> params = Maps.newHashMap();
		params.put("entityName", "entity");
		params.put("permission", "COUNT");

		directive.execute(new Environment(fakeTemplate, null, envWriter), params, new TemplateModel[0],
				new TemplateDirectiveBody()
				{
					@Override
					public void render(Writer out) throws TemplateException, IOException
					{
						out.write("PERMISSION");
					}

				});

		assertEquals(envWriter.toString(), "PERMISSION");
	}

	@Test
	public void executeWithoutPermission() throws TemplateException, IOException
	{
		when(molgenisPermissionService.hasPermissionOnEntity("entity", Permission.WRITE)).thenReturn(false);

		Map<String, Object> params = Maps.newHashMap();
		params.put("entityName", "entity");
		params.put("permission", "WRITE");

		directive.execute(new Environment(fakeTemplate, null, envWriter), params, new TemplateModel[0],
				new TemplateDirectiveBody()
				{
					@Override
					public void render(Writer out) throws TemplateException, IOException
					{
						out.write("PERMISSION");
					}

				});

		assertEquals(envWriter.toString(), "");
	}
}
