/*
 * Copyright 2022 Goldman Sachs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.authorization.InteractiveAuthorization;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ReadInteractiveService.class, name = "readInteractiveService"),
        @JsonSubTypes.Type(value = CreateInteractiveService.class, name = "createInteractiveService"),
        @JsonSubTypes.Type(value = UpdateInteractiveService.class, name = "updateInteractiveService"),
        @JsonSubTypes.Type(value = UpsertInteractiveService.class, name = "upsertInteractiveService"),
        @JsonSubTypes.Type(value = DeleteInteractiveService.class, name = "deleteInteractiveService")
})
public abstract class InteractiveService
{
    public String name;
    public InteractiveAuthorization authorization;

    public abstract <T> T accept(InteractiveServiceVisitor<T> visitor);
}
