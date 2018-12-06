package com.common.web.common.handler;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Writer;

/**
 * Created by soul on 2016. 8. 19..
 *
 * @version 1.0
 * @since 1.0
 */
public class FreemarkerExceptionHandler implements TemplateExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(FreemarkerExceptionHandler.class);

    @Override
    public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
        /*slackComponent.sendMessageToChannel(
                slackComponent.getChannelByEnv(SlackComponent.ChannelName.real_warning),
				"<< TOP ftl 에러 >>",
				te.getMessage(),
				SlackComponent.Color.RED
		);*/
    }
}
