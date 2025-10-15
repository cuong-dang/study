class Solution:
    def validWordAbbreviation(self, word: str, abbr: str) -> bool:
        if word == "" and abbr == "":
            return True
        if word == "" or abbr == "":
            return False
        if not "0" <= abbr[0] <= "9":
            return word[0] == abbr[0] and self.validWordAbbreviation(word[1:], abbr[1:])
        ai, num_abbr = 0, 0
        while ai < len(abbr) and "0" <= abbr[ai] <= "9":
            if num_abbr == 0 and abbr[ai] == "0":  # leading zero
                return False
            num_abbr = num_abbr * 10 + int(abbr[ai])
            ai += 1
        return num_abbr <= len(word) and self.validWordAbbreviation(word[num_abbr:], abbr[ai:])
