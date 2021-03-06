package org.molgenis.messageconverter;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.io.IOUtils;
import org.molgenis.data.EntityCollection;
import org.molgenis.data.csv.CsvWriter;
import org.molgenis.util.BaseHttpMessageConverter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

/**
 * Converts an EntityCollection to comma separated values
 */
public class CsvHttpMessageConverter extends BaseHttpMessageConverter<EntityCollection>
{

	public CsvHttpMessageConverter()
	{
		super(new MediaType("text", "csv", DEFAULT_CHARSET));
	}

	@Override
	protected void writeInternal(EntityCollection entities, HttpOutputMessage outputMessage) throws IOException,
			HttpMessageNotWritableException
	{
		OutputStreamWriter out = new OutputStreamWriter(outputMessage.getBody(), getCharset(outputMessage.getHeaders()));
		CsvWriter writer = new CsvWriter(out);
		try
		{
			writer.writeAttributeNames(entities.getAttributeNames());
			writer.add(entities);
		}
		finally
		{
			IOUtils.closeQuietly(writer);
		}
	}

	@Override
	protected boolean supports(Class<?> clazz)
	{
		return EntityCollection.class.isAssignableFrom(clazz);
	}

	@Override
	protected EntityCollection readInternal(Class<? extends EntityCollection> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException
	{
		throw new UnsupportedOperationException();
	}

}
