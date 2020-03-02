package com.oesia.formacion.practica.architecture.communications.processors;

import java.util.List;

import com.oesia.formacion.practica.architecture.communications.MessageManager;
import com.oesia.formacion.practica.architecture.communications.messages.put.PutMessageInformation;
import com.oesia.formacion.practica.architecture.communications.messages.put.PutMessageInformation.Builder;
import com.oesia.formacion.practica.architecture.domain.managers.article.ArticleManager;
import com.oesia.formacion.practica.architecture.domain.model.Colour;
import com.oesia.formacion.practica.architecture.domain.model.Message;
import com.oesia.formacion.practica.architecture.domain.model.Size;
import com.oesia.formacion.practica.architecture.persistence.entities.vendor.VendorData;
import com.oesia.formacion.practica.context.ContextFactory;

public class PutInformationProcessor implements Processor {

	private static final int PUT_ID_WORKORDER_POSITION = 0;
	private static final int PUT_ID_VENDOR_POSITION = 1;
	private static final int PUT_COD_ARTICLE_POSITION = 2;
	private static final int PUT_DESCRIPTION_ARTICLE_POSITION = 3;
	private static final int PUT_ID_COLOR_POSITION = 4;
	private static final int PUT_ID_TALLA_POSITION = 5;
	private static final int PUT_NUM_UDS_POSITION = 6;

//	private static final Logger LOGGER = Logger.getLogger(RemoteSender.class);

	@Override
	public void process(Message message) {

		for (List<String> row : message.getRecords()) {

			PutMessageInformation put = createPutByRow(row);

			ArticleManager articleManager = ContextFactory.getContext().get(ArticleManager.class);
			MessageManager messageManager = ContextFactory.getContext().get(MessageManager.class);
			try {
				articleManager.createArticleOrUpdateStock(put);
				messageManager.send(String.format("----- OK -----   [Work Order id: %s]\n\n", put.getIdWorkOrder()));
//				LOGGER.debug(String.format("Articulo con id: %s, introducido correctamente", idArticle));
			} catch (Exception e) {
				messageManager.send(String.format("----- KO -----   [Work Order id: %s]\n\n", put.getIdWorkOrder()));
//				LOGGER.error(String.format("Se ha producido un error al procesar la operacion PUT de articulo con id: [%s]. Error => %s", idArticle, e.getMessage()));
			}
		}
	}

	private PutMessageInformation createPutByRow(List<String> row) {

		int idWorkOrder = Integer.parseInt(row.get(PUT_ID_WORKORDER_POSITION));
		int idVendor = Integer.parseInt(row.get(PUT_ID_VENDOR_POSITION));
		int idArticle = Integer.parseInt(row.get(PUT_COD_ARTICLE_POSITION));
		String descriptionArticle = row.get(PUT_DESCRIPTION_ARTICLE_POSITION);
		int idColor = Integer.parseInt(row.get(PUT_ID_COLOR_POSITION));
		int idTalla = Integer.parseInt(row.get(PUT_ID_TALLA_POSITION));
		int numUds = Integer.parseInt(row.get(PUT_NUM_UDS_POSITION));
		Colour color = Colour.findById(idColor);
		Size talla = Size.findById(idTalla);
		VendorData vendor = VendorData.findById(idVendor);

		PutMessageInformation.Builder putBuilder = new Builder();
		putBuilder.idWorkOrder(idWorkOrder);
		putBuilder.idArticle(idArticle);
		putBuilder.idVendor(idVendor);
		putBuilder.descriptionArticle(descriptionArticle);
		putBuilder.idColor(idColor);
		putBuilder.idTalla(idTalla);
		putBuilder.numUds(numUds);
		putBuilder.color(color);
		putBuilder.talla(talla);
		putBuilder.vendor(vendor);

		return putBuilder.build();

	}

}
