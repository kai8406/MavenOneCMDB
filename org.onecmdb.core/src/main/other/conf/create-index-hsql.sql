create index derivedFromId_ci_idx on CI (derivedFromId);
create index id_derived_ci_idx on CI (id, derivedFromId);
create index id_path_ci_idx on CI (id, path);
create index path_ci_idx on CI (path);
create index alias_ci_idx on CI (alias);
create index sourceid_ci_idx on CI (sourceId);
create index targetid_ci_idx on CI (targetId);
create index lastModified_ci_idx on CI (lastModified);
create index createTime_ci_idx on CI (createTime);

create index derivedFromId_atr_idx on Attribute (derivedFromId);
create index alias_atr_idx on Attribute (alias);
create index ownerid_atr_idx on Attribute (ownerId);
create index ownerid_alias_atr_idx on Attribute (ownerId, alias);
create index valueAsString_atr_idx on Attribute (valueAsString);
create index valueAsLong_atr_idx on Attribute (valueAsLong);
create index valueAsLong_alias_atr_idx on Attribute (valueAsLong, alias);
create index lastModified_atr_idx on Attribute (lastModified);
create index createTime_atr_idx on Attribute (createTime);

create index rfc_target_and_type_idx on RFC (targetId, RFC_TYPE);
create index rfc_target_ci_and_type_idx on RFC (targetCIId, RFC_TYPE);
create index rfc_target_ci_idx on RFC (targetCIId);
create index rfc_txid_idx on RFC (txId);
