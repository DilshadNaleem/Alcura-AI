package com.Alcura.Customer.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SystemResponse
{
    @JsonProperty("matches")
    private List<SymptomInfo> matches;

    public List<SymptomInfo> getMatches()
    {
        return matches;
    }

    public void setMatches(List<SymptomInfo> matches)
    {
        this.matches = matches;
    }
}
