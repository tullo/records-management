/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import ru.yandex.qatools.htmlelements.element.TypifiedElement;

/**
 * Utility class containing helpful methods.
 *
 * @author Tuna Aksoy
 * @since 2.2
 * @version 1.0
 */
@Component
public final class Utils implements ApplicationContextAware
{
    /** application context */
    private static ApplicationContext applicationContext;

    /** default wait 10 seconds */
    private static final int DEFAULT_WAIT = 10;

    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        Utils.applicationContext = applicationContext;
    }

    /**
     * Helper to get web driver
     *
     * @return
     */
    public static WebDriver getWebDriver()
    {
        return (WebDriver)Utils.applicationContext.getBean("webDriver");
    }

    /**
     * Helper to get web driver wait object
     *
     * @return
     */
    public static WebDriverWait webDriverWait(long waitSeconds)
    {
        return new WebDriverWait(getWebDriver(), waitSeconds);
    }

    /**
     * Helper to get web driver wait object
     *
     * @return
     */
    public static WebDriverWait webDriverWait()
    {
        return webDriverWait(DEFAULT_WAIT);
    }

    /**
     * Helper method to see if element exists on page
     */
    public static boolean elementExists(By selector)
    {
        try
        {
            getWebDriver().findElement(selector);
            return true;
        }
        catch (NoSuchElementException exception)
        {
            return false;
        }
    }

    /**
     * Helper method to see if element exists within element
     */
    public static boolean elementExists(WebElement webElement, By selector)
    {
        try
        {
            webElement.findElement(selector);
            return true;
        }
        catch (NoSuchElementException exception)
        {
            return false;
        }
        catch (StaleElementReferenceException exception)
        {
            webElement.findElement(selector);
            return true;
        }
    }

    public static <T> void waitFor(ExpectedCondition<T> condition)
    {
        webDriverWait().until(condition);
    }

    /**
     * Helper method to wait for the staleness of an element
     */
    public static void waitForStalenessOf(WebElement webElement)
    {
        webDriverWait().until(ExpectedConditions.stalenessOf(webElement));
    }

    /**
     * @see Utils#waitForStalenessOf(WebElement)
     */
    public static void waitForStalenessOf(TypifiedElement webElement)
    {
        waitForStalenessOf(webElement.getWrappedElement());
    }

    /**
     * Helper method to wait for the visibility of an element
     */
    public static void waitForVisibilityOf(WebElement webElement)
    {
        webDriverWait().until(ExpectedConditions.visibilityOf(webElement));
    }

    /**
     * @see Utils#waitForVisibilityOf(WebElement)
     */
    public static void waitForVisibilityOf(TypifiedElement webElement)
    {
        waitForVisibilityOf(webElement.getWrappedElement());
    }

    /**
     * Helper method to wait for the visibility of element located
     * by selector
     */
    public static WebElement waitForVisibilityOf(By locator)
    {
        return webDriverWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Helper method to wait for the invisibility of an element
     */
    public static void waitForInvisibilityOf(WebElement webElement)
    {
        webDriverWait().until(ExpectedConditions.not(ExpectedConditions.visibilityOf(webElement)));
    }

    /**
     * Helper method to wait for the invisibility of an element located
     * by selector
     */
    public static void waitForInvisibilityOf(By locator)
    {
        webDriverWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Helper to mouse over element
     *
     * @param webElement
     * @return
     */
    public static <T extends WebElement> T mouseOver(T webElement)
    {
        Actions actions = new Actions(getWebDriver());
        actions.moveToElement(webElement).perform();
        return webElement;
    }

    /**
     * Helper method to mouse over element
     *
     * @param wrapsElement
     * @return
     */
    public static <T extends WrapsElement> T mouseOver(T wrapsElement)
    {
        mouseOver(wrapsElement.getWrappedElement());
        return wrapsElement;
    }

    /**
     * Clear control
     */
    public static <T extends WebElement> T clear(T field)
    {
        checkMandatoryParam("field", field);
        field.clear();
        return field;
    }

    /**
     * Clear control and enter text
     */
    public static <T extends WebElement> T clearAndType(T field, String text)
    {
        clear(field);

        checkMandatoryParam("text", text);
        field.sendKeys(text);
        return field;
    }

    /**
     * Clear control and enter text
     */
    public static <T extends WrapsElement> T clear(T field)
    {
        clear(field.getWrappedElement());
        return field;
    }

    /**
     * Clear control and enter text
     */
    public static <T extends WrapsElement> T clearAndType(T field, String text)
    {
        clearAndType(field.getWrappedElement(), text);
        return field;
    }

    /**
     * Checks that the provided parameter satisfies all mandatory preconditions. These include:
     * <ul>
     *     <li>{@code paramName} should be non-null and not blank.</li>
     *     <li>{@code object} should be non-null.</li>
     *     <li>String {@code object}s should not be blank.</li>
     *     <li>Collection {@code object}s should not be empty.</li>
     * </ul>
     *
     * @param paramName parameter name
     * @param object object value
     * @throws IllegalArgumentException if any violations have occurred.
     */
    public static void checkMandatoryParam(final String paramName, final Object object)
    {
        if (StringUtils.isBlank(paramName))
        {
            throw new IllegalArgumentException(String.format(
                "The parameter paramName is required and can not be'%s'", paramName));
        }
        if (object == null)
        {
            throw new IllegalArgumentException(String.format(
                "'%s' is a mandatory parameter and must have a value", paramName));
        }
        if (object instanceof String && StringUtils.isBlank((String) object))
        {
            throw new IllegalArgumentException(
                String.format("'%s' is a mandatory parameter", paramName));
        }
        if (object instanceof Collection<?> && ((Collection<?>) object).isEmpty())
        {
            throw new IllegalArgumentException(
                String.format("'%s' is a mandatory parameter and can not be empty", paramName));
        }
    }

    /**
     * Create temp file TODO .. support multiple mimetypes .. build files with
     * real size content
     *
     * @param name file name
     * @return {@link File} file
     */
    public static File createTempFile(final String name)
    {
        try
        {
            // create file
            final File file = File.createTempFile(name, ".txt");

            // create writer
            try (FileOutputStream   fos    = new FileOutputStream(file);
                 OutputStreamWriter writer = new OutputStreamWriter(fos, Charset.forName("UTF-8").newEncoder()))
            {
                // place content in file
                writer.write("this is a sample test upload file");
            }

            return file;
        }
        catch (Exception exception)
        {
            throw new RuntimeException("Unable to create test file.", exception);
        }
    }

    /**
     * Helper method to retry the provided {@link Retry code block}, ignoring failures until either
     * the code block completes successfully or the maximum number of retries has been reached.
     * are used up
     *
     * @param <T>       the return type from the code block.
     * @param retry     a code block to execute.
     * @param count     maximum number of retries.
     * @return          result of the code block.
     */
    public static final <T> T retry(Retry<T> retry, int count)
    {
        int attempt = 0;

        while (true)
        {
            try
            {
                // try and execute
                return retry.execute();
            }
            catch (Exception exception)
            {
                attempt++;
                // if we have used up all our retries throw the exception
                if (attempt >= count)
                {
                    throw exception;
                }

                // otherwise do nothing and try again
            }
        }
    }
}
