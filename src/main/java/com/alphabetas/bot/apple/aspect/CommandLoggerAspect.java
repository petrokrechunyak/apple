package com.alphabetas.bot.apple.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class CommandLoggerAspect {

    @Pointcut("execution(void execute(org.telegram.telegrambots.meta.api.objects.Update))")
    private void pointCutOnExecute() {}

    @Before("pointCutOnExecute()")
    public void executeLogger(JoinPoint point) {
        String text = String.format("Entered into %s with text: ", point.getSignature().getName());
        log.info(text);

    }

}
